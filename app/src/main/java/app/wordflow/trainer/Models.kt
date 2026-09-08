package app.wordflow.trainer

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LanguageInfo(
    val id: String,
    val name: String,
    val level: String,
    val flag: String,
    val speech: String = "en-US",
    val isAvailableInStore: Boolean = true
)

val LANGUAGES = listOf(
    LanguageInfo("la", "Latein", "A1-C1", "📜"),
    LanguageInfo("es", "Spanisch", "B1", "🇪🇸", "es-ES"),
    LanguageInfo("fr", "Französisch", "A2", "🇫🇷", "fr-FR"),
    LanguageInfo("en", "Englisch", "B1", "🇬🇧", "en-GB"),
    LanguageInfo("it", "Italienisch", "A1", "🇮🇹", "it-IT"),
    LanguageInfo("ja", "Japanisch", "A1", "🇯🇵", "ja-JP"),
    LanguageInfo("pt", "Portugiesisch", "A2", "🇵🇹", "pt-PT"),
    LanguageInfo("ru", "Russisch", "A1", "🇷🇺", "ru-RU"),
    LanguageInfo("zh", "Chinesisch", "A1", "🇨🇳", "zh-CN")
)

fun languageById(id: String): LanguageInfo =
    LANGUAGES.firstOrNull { it.id == id } ?: LANGUAGES.first()

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val lang: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val lang: String,
    val word: String,
    val translation: String,
    val phonetic: String = "",
    val pos: String = "Vokabel",
    val gender: String = "",
    val example: String = "",
    val exampleDe: String = "",
    val box: Int = 0,
    val source: String = "manual",
    val chapterId: String = ""
)

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val langId: String,
    val title: String,
    val words: Int,
    val accuracy: Int,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey val date: String,
    val count: Int
)

data class UserState(
    val onboardingDone: Boolean = false,
    val name: String = "",
    val selectedLang: String = "la",
    val selectedLangIds: List<String> = emptyList(),
    val dailyGoal: Int = 24,
    val isPlus: Boolean = false,
    val streak: Int = 0,
    val lastStudyDate: String? = null,
    val todayDate: String = "",
    val todayCount: Int = 0,
    val todayCorrect: Int = 0,
    val todayWrong: Int = 0,
    val totalCorrect: Int = 0,
    val totalAttempts: Int = 0,
    val totalSeconds: Long = 0,
    val cyloneSub: String = "",
    val cyloneEmail: String = "",
    val cyloneName: String = "",
    val cyloneAccessToken: String = "",
    val cyloneRefreshToken: String = ""
) {
    val isCyloneLinked: Boolean get() = cyloneSub.isNotBlank() || cyloneEmail.isNotBlank()
    val selectedLanguages: List<LanguageInfo>
        get() = selectedLangIds.mapNotNull { id -> LANGUAGES.firstOrNull { it.id == id } }
}

data class LanguagePack(
    val id: String,
    val langId: String,
    val name: String,
    val description: String,
    val price: String = "Kostenlos",
    val chapters: List<String> = emptyList()
)

data class VocabPair(val word: String, val translation: String)

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
