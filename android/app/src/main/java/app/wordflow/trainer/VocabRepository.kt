package app.wordflow.trainer

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class VocabRepository(context: Context, private val db: AppDatabase) {
    private val prefs = UserPrefs(context)
    val user: Flow<UserState> = prefs.state
    val activity: Flow<List<ActivityEntity>> = db.activity().observe()
    val daily: Flow<List<DailyEntity>> = db.daily().observe()

    fun words(lang: String): Flow<List<WordEntity>> = db.words().observeByLang(lang)
    fun allWords(): Flow<List<WordEntity>> = db.words().observeAll()

    init {
        CoroutineScope(Dispatchers.IO).launch { seedIfNeeded() }
    }

    private suspend fun seedIfNeeded() {
        if (db.words().count() == 0) {
            db.words().insertAll(VocabSeed.all())
        }
        prefs.save { state ->
            val today = todayKey()
            if (state.todayDate == today) state
            else state.copy(todayDate = today, todayCount = 0, todayCorrect = 0, todayWrong = 0)
        }
    }

    suspend fun completeOnboarding(name: String, lang: String, goal: Int) {
        prefs.save {
            it.copy(
                onboardingDone = true,
                name = name.trim().ifBlank { "Anna" }.take(24),
                selectedLang = lang,
                dailyGoal = goal,
            )
        }
    }

    suspend fun selectLang(lang: String) {
        prefs.save { it.copy(selectedLang = lang) }
    }

    suspend fun setPro(enabled: Boolean) {
        prefs.save { state ->
            val lang = if (!enabled && LANGUAGES.first { it.id == state.selectedLang }.free.not()) "es" else state.selectedLang
            state.copy(isPro = enabled, selectedLang = lang)
        }
    }

    suspend fun importPairs(lang: String, pairs: List<VocabPair>) {
        val words = pairs.mapIndexed { index, pair ->
            WordEntity(
                id = "scan-$lang-${pair.word.lowercase().replace(Regex("[^a-z0-9äöü]+"), "-")}-$index-${System.currentTimeMillis()}",
                lang = lang,
                word = pair.word,
                translation = pair.translation,
                pos = "Scan",
                source = "scan",
            )
        }
        db.words().insertAll(words)
    }

    suspend fun grade(word: WordEntity, knew: Boolean, elapsedMs: Long = 0) {
        val nextBox = if (knew) maxOf(2, minOf(5, word.box + 1)) else 1
        db.words().updateBox(word.id, nextBox)
        val today = todayKey()
        prefs.save { state ->
            var streak = state.streak
            var last = state.lastStudyDate
            if (last != today) {
                streak = if (last == yesterdayKey()) streak + 1 else 1
                last = today
            }
            val rolled = if (state.todayDate == today) state else state.copy(
                todayDate = today, todayCount = 0, todayCorrect = 0, todayWrong = 0,
            )
            rolled.copy(
                streak = streak,
                lastStudyDate = last,
                todayCount = rolled.todayCount + 1,
                todayCorrect = rolled.todayCorrect + if (knew) 1 else 0,
                todayWrong = rolled.todayWrong + if (knew) 0 else 1,
                totalCorrect = rolled.totalCorrect + if (knew) 1 else 0,
                totalAttempts = rolled.totalAttempts + 1,
                totalSeconds = rolled.totalSeconds + elapsedMs / 1000,
            )
        }
        val existing = db.daily().get(today)
        db.daily().upsert(DailyEntity(today, (existing?.count ?: 0) + 1))
    }

    suspend fun logSession(lang: LanguageInfo, known: Int, total: Int, seconds: Long) {
        prefs.save { it.copy(totalSeconds = it.totalSeconds + seconds) }
        db.activity().insert(
            ActivityEntity(
                langId = lang.id,
                title = "${lang.name} · ${lang.pack}",
                words = total,
                accuracy = if (total == 0) 0 else (known * 100 / total),
                date = todayKey(),
            )
        )
    }

    suspend fun learnedCount(lang: String) = db.words().learnedCount(lang)
    suspend fun totalCount(lang: String) = db.words().totalCount(lang)
    suspend fun learnedTotal() = db.words().learnedTotal()
    suspend fun byLang(lang: String) = db.words().byLang(lang)
    suspend fun userOnce() = prefs.state.first()

    companion object {
        fun todayKey(): String = LocalDate.now().toString()
        fun yesterdayKey(): String = LocalDate.now().minusDays(1).toString()
        fun longDate(): String {
            val d = LocalDate.now()
            val day = d.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN)
            return "${day.replaceFirstChar { it.titlecase(Locale.GERMAN) }} · ${d.dayOfMonth}. ${d.month.getDisplayName(TextStyle.FULL, Locale.GERMAN)}"
        }
        fun formatWhen(iso: String): String {
            val today = LocalDate.now()
            val then = LocalDate.parse(iso)
            return when (then) {
                today -> "Heute"
                today.minusDays(1) -> "Gestern"
                else -> then.format(DateTimeFormatter.ofPattern("d. MMM", Locale.GERMAN))
            }
        }
    }
}
