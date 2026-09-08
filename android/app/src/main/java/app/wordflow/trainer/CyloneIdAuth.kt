package app.wordflow.trainer

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object CyloneIdAuth {
    const val REDIRECT_URI = "app.wordflow.trainer://oauth/callback"
    private const val AUTHORIZE = "https://login.cylone.de/oauth/authorize"
    private const val TOKEN = "https://login.cylone.de/oauth/token"
    private const val USERINFO = "https://login.cylone.de/oauth/userinfo"
    private const val SCOPE = "openid profile email offline_access"

    private val _events = MutableSharedFlow<Result<CyloneProfile>>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    @Volatile private var verifier: String? = null
    @Volatile private var expectedState: String? = null
    @Volatile private var pending = false

    fun isConfigured(): Boolean = BuildConfig.CYLONE_CLIENT_ID.isNotBlank()

    fun startLogin(activity: Activity) {
        if (!isConfigured()) {
            _events.tryEmit(
                Result.failure(
                    IllegalStateException(
                        "Cylone ID ist noch nicht hinterlegt. Trage cylone.clientId in local.properties ein.",
                    ),
                ),
            )
            return
        }
        val codeVerifier = randomUrl(32)
        val state = randomUrl(24)
        verifier = codeVerifier
        expectedState = state
        pending = true
        val challenge = sha256Url(codeVerifier)
        val uri = Uri.parse(AUTHORIZE).buildUpon()
            .appendQueryParameter("client_id", BuildConfig.CYLONE_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("scope", SCOPE)
            .build()
        CustomTabsIntent.Builder().build().launchUrl(activity, uri)
    }

    fun handleRedirect(uri: Uri?) {
        if (uri == null || !pending) return
        if (uri.scheme != "app.wordflow.trainer") return
        pending = false
        val error = uri.getQueryParameter("error")
        if (!error.isNullOrBlank()) {
            val desc = uri.getQueryParameter("error_description") ?: error
            _events.tryEmit(Result.failure(IllegalStateException(desc)))
            return
        }
        val state = uri.getQueryParameter("state")
        if (state.isNullOrBlank() || state != expectedState) {
            _events.tryEmit(Result.failure(IllegalStateException("Ungültiger Login-Status. Bitte erneut versuchen.")))
            return
        }
        val code = uri.getQueryParameter("code")
        val codeVerifier = verifier
        if (code.isNullOrBlank() || codeVerifier.isNullOrBlank()) {
            _events.tryEmit(Result.failure(IllegalStateException("Kein Authorization Code erhalten.")))
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokens = exchangeCode(code, codeVerifier)
                val profile = fetchProfile(tokens.accessToken)
                _events.emit(
                    Result.success(
                        profile.copy(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken,
                        ),
                    ),
                )
            } catch (e: Exception) {
                _events.emit(Result.failure(e))
            }
        }
    }

    private data class Tokens(val accessToken: String, val refreshToken: String)

    private fun exchangeCode(code: String, codeVerifier: String): Tokens {
        val body = buildString {
            append("grant_type=authorization_code")
            append("&code=").append(enc(code))
            append("&redirect_uri=").append(enc(REDIRECT_URI))
            append("&client_id=").append(enc(BuildConfig.CYLONE_CLIENT_ID))
            append("&code_verifier=").append(enc(codeVerifier))
            if (BuildConfig.CYLONE_CLIENT_SECRET.isNotBlank()) {
                append("&client_secret=").append(enc(BuildConfig.CYLONE_CLIENT_SECRET))
            }
        }
        val json = postForm(TOKEN, body)
        val access = json.optString("access_token")
        if (access.isBlank()) {
            throw IllegalStateException(json.optString("error_description", "Token-Austausch fehlgeschlagen."))
        }
        return Tokens(access, json.optString("refresh_token"))
    }

    private fun fetchProfile(accessToken: String): CyloneProfile {
        val conn = (URL(USERINFO).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $accessToken")
            connectTimeout = 15_000
            readTimeout = 15_000
        }
        val payload = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()
        val json = JSONObject(payload)
        val name = json.optString("name")
            .ifBlank { json.optString("preferred_username") }
            .ifBlank { json.optString("email").substringBefore("@") }
        return CyloneProfile(
            sub = json.optString("sub"),
            name = name,
            email = json.optString("email"),
        )
    }

    private fun postForm(url: String, body: String): JSONObject {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connectTimeout = 15_000
            readTimeout = 15_000
        }
        conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
        val payload = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        conn.disconnect()
        return if (payload.isBlank()) JSONObject() else JSONObject(payload)
    }

    private fun randomUrl(bytes: Int): String {
        val buffer = ByteArray(bytes)
        SecureRandom().nextBytes(buffer)
        return Base64.encodeToString(buffer, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun sha256Url(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun enc(value: String): String = URLEncoder.encode(value, "UTF-8")
}
