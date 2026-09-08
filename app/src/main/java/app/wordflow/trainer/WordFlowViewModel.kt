package app.wordflow.trainer

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class SessionState(
    val lang: LanguageInfo,
    val chapterName: String,
    val chapterId: String,
    val queue: List<WordEntity>,
    val index: Int = 0,
    val flipped: Boolean = false,
    val results: Map<String, Boolean> = emptyMap(),
    val startedAt: Long = System.currentTimeMillis(),
    val done: Boolean = false,
) {
    val current: WordEntity? get() = queue.getOrNull(index)
    val known: Int get() = results.values.count { it }
}

data class UiState(
    val user: UserState = UserState(),
    val words: List<WordEntity> = emptyList(),
    val chapters: List<ChapterEntity> = emptyList(),
    val activity: List<ActivityEntity> = emptyList(),
    val daily: List<DailyEntity> = emptyList(),
    val learnedByLang: Map<String, Pair<Int, Int>> = emptyMap(),
    val learnedTotal: Int = 0,
    val session: SessionState? = null,
    val ready: Boolean = false,
    val authMessage: String? = null,
    val authBusy: Boolean = false,
    val cyloneConfigured: Boolean = CyloneIdAuth.isConfigured(),
)

private data class Quad(
    val user: UserState,
    val session: SessionState?,
    val activity: List<ActivityEntity>,
    val daily: List<DailyEntity>,
)

class WordFlowViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WordFlowApplication).repository
    private val extra = MutableStateFlow<SessionState?>(null)
    private val authMessage = MutableStateFlow<String?>(null)
    private val authBusy = MutableStateFlow(false)
    private var tts: TextToSpeech? = null

    val ui: StateFlow<UiState> = combine(
        combine(repo.user, extra, repo.activity, repo.daily) { user, session, activity, daily ->
            Quad(user, session, activity, daily)
        },
        repo.allWords(),
        repo.chapters,
        authMessage,
        authBusy,
    ) { quad, allWords, chapters, message, busy ->
        val visibleLangs = quad.user.selectedLangIds.toSet()
        val words = allWords.filter { it.lang in visibleLangs }
        val learned = LANGUAGES.associate { lang ->
            val list = allWords.filter { it.lang == lang.id }
            lang.id to (list.count { it.box >= 2 } to list.size)
        }
        UiState(
            user = quad.user,
            words = words,
            chapters = chapters.filter { it.lang in visibleLangs },
            activity = quad.activity,
            daily = quad.daily,
            learnedByLang = learned,
            learnedTotal = allWords.count { it.box >= 2 },
            session = quad.session,
            ready = true,
            authMessage = message,
            authBusy = busy,
            cyloneConfigured = CyloneIdAuth.isConfigured(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        tts = TextToSpeech(getApplication()) { }
        viewModelScope.launch {
            CyloneIdAuth.events.collect { result ->
                authBusy.value = false
                result.onSuccess { profile ->
                    repo.linkCylone(profile)
                    authMessage.value = "Angemeldet als ${profile.email.ifBlank { profile.name }}"
                }.onFailure { error ->
                    authMessage.value = error.message ?: "Anmeldung fehlgeschlagen."
                }
            }
        }
    }

    fun completeOnboarding(name: String, langIds: List<String>, goal: Int) {
        viewModelScope.launch { repo.completeOnboarding(name, langIds, goal) }
    }

    fun selectLang(id: String) {
        viewModelScope.launch { repo.selectLang(id) }
    }

    fun addLanguage(id: String) {
        viewModelScope.launch { repo.addLanguage(id) }
    }

    fun createChapter(langId: String, name: String) {
        viewModelScope.launch { repo.createChapter(langId, name) }
    }

    fun addWord(langId: String, chapterId: String, word: String, translation: String) {
        viewModelScope.launch { repo.addWord(langId, chapterId, word, translation) }
    }

    fun setPlus(enabled: Boolean) {
        viewModelScope.launch { repo.setPlus(enabled) }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch { repo.setDailyGoal(goal) }
    }

    fun setName(name: String) {
        viewModelScope.launch { repo.setName(name) }
    }

    fun markAuthBusy() {
        authBusy.value = true
        authMessage.value = null
    }

    fun unlinkCylone() {
        viewModelScope.launch { repo.unlinkCylone() }
    }

    fun startSession(chapterId: String? = null, langId: String? = null) {
        viewModelScope.launch {
            val user = repo.userOnce()
            val lang = languageById(langId ?: user.selectedLang)
            val chapter = chapterId?.let { repo.chapterById(it) }
            val words = when {
                chapter != null -> repo.byChapter(chapter.id)
                else -> repo.byLang(lang.id)
            }.sortedBy { it.box }.take(12)
            if (words.isEmpty()) return@launch
            extra.value = SessionState(
                lang = lang,
                chapterName = chapter?.name ?: "Alle Kapitel",
                chapterId = chapter?.id.orEmpty(),
                queue = words,
            )
        }
    }

    fun flip() {
        extra.value = extra.value?.copy(flipped = !(extra.value?.flipped ?: false))
    }

    fun grade(knew: Boolean) {
        viewModelScope.launch {
            val session = extra.value ?: return@launch
            val word = session.current ?: return@launch
            repo.grade(word, knew)
            val results = session.results + (word.id to knew)
            val nextIndex = session.index + 1
            if (nextIndex >= session.queue.size) {
                val known = results.values.count { it }
                repo.logSession(session.lang, session.chapterName, known, results.size, (System.currentTimeMillis() - session.startedAt) / 1000)
                extra.value = session.copy(index = nextIndex, results = results, done = true)
            } else {
                extra.value = session.copy(index = nextIndex, results = results, flipped = false)
            }
        }
    }

    fun endSession() {
        extra.value = null
    }

    fun speak(word: WordEntity, lang: LanguageInfo) {
        val engine = tts ?: return
        engine.language = Locale.forLanguageTag(lang.speech)
        engine.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, word.id)
    }

    fun importPairs(lang: String, chapterId: String, pairs: List<VocabPair>) {
        viewModelScope.launch { repo.importPairs(lang, chapterId, pairs) }
    }

    suspend fun recognizeText(bitmap: Bitmap, preferJapanese: Boolean): String {
        val client = if (preferJapanese) {
            TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        } else {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }
        return try {
            suspendCancellableCoroutine { cont ->
                client.process(InputImage.fromBitmap(bitmap, 0))
                    .addOnSuccessListener { cont.resume(it.text) }
                    .addOnFailureListener { e -> cont.resumeWithException(e) }
            }
        } finally {
            client.close()
        }
    }

    override fun onCleared() {
        tts?.shutdown()
        super.onCleared()
    }
}
