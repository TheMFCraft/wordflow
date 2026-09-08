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
    val chapters: Flow<List<ChapterEntity>> = db.chapters().observeAll()

    fun words(lang: String): Flow<List<WordEntity>> = db.words().observeByLang(lang)
    fun allWords(): Flow<List<WordEntity>> = db.words().observeAll()

    init {
        CoroutineScope(Dispatchers.IO).launch { resetTodayIfNeeded() }
    }

    private suspend fun resetTodayIfNeeded() {
        prefs.save { state ->
            val today = todayKey()
            if (state.todayDate == today) state
            else state.copy(todayDate = today, todayCount = 0, todayCorrect = 0, todayWrong = 0)
        }
    }

    suspend fun completeOnboarding(name: String, langIds: List<String>, goal: Int) {
        val langs = langIds.filter { id -> LANGUAGES.any { it.id == id } }.distinct()
            .ifEmpty { listOf("es") }
        prefs.save {
            it.copy(
                onboardingDone = true,
                name = name.trim().ifBlank { it.cyloneName.ifBlank { "Anna" } }.take(24),
                selectedLang = langs.first(),
                selectedLangIds = langs,
                dailyGoal = goal,
            )
        }
        langs.forEach { ensureLanguageContent(it) }
    }

    suspend fun addLanguage(langId: String) {
        if (LANGUAGES.none { it.id == langId }) return
        prefs.save { state ->
            val ids = (state.selectedLangIds + langId).distinct()
            state.copy(selectedLangIds = ids, selectedLang = langId)
        }
        ensureLanguageContent(langId)
    }

    suspend fun createChapter(langId: String, name: String): ChapterEntity? {
        val trimmed = name.trim().ifBlank { return null }
        if (LANGUAGES.none { it.id == langId }) return null
        prefs.save { state ->
            val ids = if (langId in state.selectedLangIds) state.selectedLangIds else state.selectedLangIds + langId
            state.copy(selectedLangIds = ids, selectedLang = langId)
        }
        ensureLanguageContent(langId)
        val chapter = ChapterEntity(
            id = "ch-$langId-${System.currentTimeMillis()}",
            lang = langId,
            name = trimmed.take(40),
        )
        db.chapters().insert(chapter)
        return chapter
    }

    suspend fun addWord(langId: String, chapterId: String, word: String, translation: String) {
        val w = word.trim()
        val t = translation.trim()
        if (w.isBlank() || t.isBlank()) return
        var targetChapter = chapterId
        if (targetChapter.isBlank() || db.chapters().byId(targetChapter) == null) {
            ensureLanguageContent(langId)
            targetChapter = db.chapters().byLang(langId).firstOrNull()?.id ?: return
        }
        db.words().insertAll(
            listOf(
                WordEntity(
                    id = "manual-$langId-${w.lowercase().replace(Regex("[^a-z0-9äöüß]+"), "-")}-${System.currentTimeMillis()}",
                    lang = langId,
                    word = w,
                    translation = t,
                    pos = "Vokabel",
                    source = "manual",
                    chapterId = targetChapter,
                ),
            ),
        )
        prefs.save { it.copy(selectedLang = langId, selectedLangIds = (it.selectedLangIds + langId).distinct()) }
    }

    private suspend fun ensureLanguageContent(langId: String) {
        val existing = db.chapters().byLang(langId)
        if (existing.isNotEmpty()) return
        val chapter = ChapterEntity(
            id = "ch-$langId-grund",
            lang = langId,
            name = "Grundwortschatz",
        )
        db.chapters().insert(chapter)
        val seeds = VocabSeed.forLang(langId).map { it.copy(chapterId = chapter.id) }
        if (seeds.isNotEmpty()) db.words().insertAll(seeds)
    }

    suspend fun selectLang(lang: String) {
        prefs.save { it.copy(selectedLang = lang) }
    }

    suspend fun setPlus(enabled: Boolean) {
        prefs.save { it.copy(isPlus = enabled) }
    }

    suspend fun setDailyGoal(goal: Int) {
        prefs.save { it.copy(dailyGoal = goal) }
    }

    suspend fun setName(name: String) {
        prefs.save { it.copy(name = name.trim().take(24)) }
    }

    suspend fun linkCylone(profile: CyloneProfile) {
        prefs.save { state ->
            state.copy(
                cyloneSub = profile.sub,
                cyloneEmail = profile.email,
                cyloneName = profile.name,
                cyloneAccessToken = profile.accessToken,
                cyloneRefreshToken = profile.refreshToken,
                name = state.name.ifBlank { profile.name.take(24) },
            )
        }
    }

    suspend fun unlinkCylone() {
        prefs.save {
            it.copy(
                cyloneSub = "",
                cyloneEmail = "",
                cyloneName = "",
                cyloneAccessToken = "",
                cyloneRefreshToken = "",
            )
        }
    }

    suspend fun importPairs(lang: String, chapterId: String, pairs: List<VocabPair>) {
        var target = chapterId
        if (target.isBlank() || db.chapters().byId(target) == null) {
            ensureLanguageContent(lang)
            target = db.chapters().byLang(lang).firstOrNull()?.id.orEmpty()
        }
        val words = pairs.mapIndexed { index, pair ->
            WordEntity(
                id = "scan-$lang-${pair.word.lowercase().replace(Regex("[^a-z0-9äöü]+"), "-")}-$index-${System.currentTimeMillis()}",
                lang = lang,
                word = pair.word,
                translation = pair.translation,
                pos = "Scan",
                source = "scan",
                chapterId = target,
            )
        }
        db.words().insertAll(words)
        prefs.save { it.copy(selectedLang = lang, selectedLangIds = (it.selectedLangIds + lang).distinct()) }
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

    suspend fun logSession(lang: LanguageInfo, chapterName: String, known: Int, total: Int, seconds: Long) {
        prefs.save { it.copy(totalSeconds = it.totalSeconds + seconds) }
        db.activity().insert(
            ActivityEntity(
                langId = lang.id,
                title = "${lang.name} · $chapterName",
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
    suspend fun byChapter(chapterId: String) = db.words().byChapter(chapterId)
    suspend fun chapterById(id: String) = db.chapters().byId(id)
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
