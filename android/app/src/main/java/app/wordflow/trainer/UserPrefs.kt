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
    private val goal = intPreferencesKey("goal")
    private val pro = booleanPreferencesKey("pro")
    private val streak = intPreferencesKey("streak")
    private val lastStudy = stringPreferencesKey("last_study")
    private val todayDate = stringPreferencesKey("today_date")
    private val todayCount = intPreferencesKey("today_count")
    private val todayCorrect = intPreferencesKey("today_correct")
    private val todayWrong = intPreferencesKey("today_wrong")
    private val totalCorrect = intPreferencesKey("total_correct")
    private val totalAttempts = intPreferencesKey("total_attempts")
    private val totalSeconds = longPreferencesKey("total_seconds")

    val state: Flow<UserState> = context.dataStore.data.map { p ->
        UserState(
            onboardingDone = p[onboarding] == true,
            name = p[name].orEmpty(),
            selectedLang = p[lang] ?: "es",
            dailyGoal = p[goal] ?: 24,
            isPro = p[pro] == true,
            streak = p[streak] ?: 0,
            lastStudyDate = p[lastStudy],
            todayDate = p[todayDate].orEmpty(),
            todayCount = p[todayCount] ?: 0,
            todayCorrect = p[todayCorrect] ?: 0,
            todayWrong = p[todayWrong] ?: 0,
            totalCorrect = p[totalCorrect] ?: 0,
            totalAttempts = p[totalAttempts] ?: 0,
            totalSeconds = p[totalSeconds] ?: 0L,
        )
    }

    suspend fun save(update: (UserState) -> UserState) {
        context.dataStore.edit { p ->
            val current = UserState(
                onboardingDone = p[onboarding] == true,
                name = p[name].orEmpty(),
                selectedLang = p[lang] ?: "es",
                dailyGoal = p[goal] ?: 24,
                isPro = p[pro] == true,
                streak = p[streak] ?: 0,
                lastStudyDate = p[lastStudy],
                todayDate = p[todayDate].orEmpty(),
                todayCount = p[todayCount] ?: 0,
                todayCorrect = p[todayCorrect] ?: 0,
                todayWrong = p[todayWrong] ?: 0,
                totalCorrect = p[totalCorrect] ?: 0,
                totalAttempts = p[totalAttempts] ?: 0,
                totalSeconds = p[totalSeconds] ?: 0L,
            )
            val next = update(current)
            p[onboarding] = next.onboardingDone
            p[name] = next.name
            p[lang] = next.selectedLang
            p[goal] = next.dailyGoal
            p[pro] = next.isPro
            p[streak] = next.streak
            if (next.lastStudyDate != null) p[lastStudy] = next.lastStudyDate else p.remove(lastStudy)
            p[todayDate] = next.todayDate
            p[todayCount] = next.todayCount
            p[todayCorrect] = next.todayCorrect
            p[todayWrong] = next.todayWrong
            p[totalCorrect] = next.totalCorrect
            p[totalAttempts] = next.totalAttempts
            p[totalSeconds] = next.totalSeconds
        }
    }
}
