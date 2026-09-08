package app.wordflow.trainer

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("wordflow_prefs")

class UserPrefs(private val context: Context) {
    private val onboarding = booleanPreferencesKey("onboarding")
    private val name = stringPreferencesKey("name")
    private val lang = stringPreferencesKey("lang")
    private val langs = stringPreferencesKey("langs")
    private val goal = intPreferencesKey("goal")
    private val plus = booleanPreferencesKey("plus")
    private val streak = intPreferencesKey("streak")
    private val lastStudy = stringPreferencesKey("last_study")
    private val todayDate = stringPreferencesKey("today_date")
    private val todayCount = intPreferencesKey("today_count")
    private val todayCorrect = intPreferencesKey("today_correct")
    private val todayWrong = intPreferencesKey("today_wrong")
    private val totalCorrect = intPreferencesKey("total_correct")
    private val totalAttempts = intPreferencesKey("total_attempts")
    private val totalSeconds = longPreferencesKey("total_seconds")
    private val theme = stringPreferencesKey("theme")
    private val appLang = stringPreferencesKey("app_lang")
    private val cyloneSub = stringPreferencesKey("cylone_sub")
    private val cyloneEmail = stringPreferencesKey("cylone_email")
    private val cyloneName = stringPreferencesKey("cylone_name")
    private val cyloneAccess = stringPreferencesKey("cylone_access")
    private val cyloneRefresh = stringPreferencesKey("cylone_refresh")

    val state: Flow<UserState> = context.dataStore.data.map { p ->
        fromPrefs(p)
    }

    suspend fun save(update: (UserState) -> UserState) {
        context.dataStore.edit { p ->
            val next = update(fromPrefs(p))
            p[onboarding] = next.onboardingDone
            p[name] = next.name
            p[lang] = next.selectedLang
            p[langs] = next.selectedLangIds.joinToString(",")
            p[goal] = next.dailyGoal
            p[plus] = next.isPlus
            p[streak] = next.streak
            if (next.lastStudyDate != null) p[lastStudy] = next.lastStudyDate else p.remove(lastStudy)
            p[todayDate] = next.todayDate
            p[todayCount] = next.todayCount
            p[todayCorrect] = next.todayCorrect
            p[todayWrong] = next.todayWrong
            p[totalCorrect] = next.totalCorrect
            p[totalAttempts] = next.totalAttempts
            p[totalSeconds] = next.totalSeconds
            p[theme] = next.theme
            p[appLang] = next.appLanguage
            p[cyloneSub] = next.cyloneSub
            p[cyloneEmail] = next.cyloneEmail
            p[cyloneName] = next.cyloneName
            p[cyloneAccess] = next.cyloneAccessToken
            p[cyloneRefresh] = next.cyloneRefreshToken
        }
    }

    private fun fromPrefs(p: androidx.datastore.preferences.core.Preferences): UserState {
        val selected = p[lang] ?: "la"
        val ids = p[langs].orEmpty().split(",").map { it.trim() }.filter { it.isNotBlank() }
            .ifEmpty { if (p[onboarding] == true) listOf(selected) else emptyList() }
        return UserState(
            onboardingDone = p[onboarding] == true,
            name = p[name].orEmpty(),
            selectedLang = selected,
            selectedLangIds = ids,
            dailyGoal = p[goal] ?: 24,
            isPlus = p[plus] == true,
            streak = p[streak] ?: 0,
            lastStudyDate = p[lastStudy],
            todayDate = p[todayDate].orEmpty(),
            todayCount = p[todayCount] ?: 0,
            todayCorrect = p[todayCorrect] ?: 0,
            todayWrong = p[todayWrong] ?: 0,
            totalCorrect = p[totalCorrect] ?: 0,
            totalAttempts = p[totalAttempts] ?: 0,
            totalSeconds = p[totalSeconds] ?: 0L,
            theme = p[theme] ?: "system",
            appLanguage = p[appLang] ?: "de",
            cyloneSub = p[cyloneSub].orEmpty(),
            cyloneEmail = p[cyloneEmail].orEmpty(),
            cyloneName = p[cyloneName].orEmpty(),
            cyloneAccessToken = p[cyloneAccess].orEmpty(),
            cyloneRefreshToken = p[cyloneRefresh].orEmpty(),
        )
    }
}
