package app.wordflow.trainer

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LanguageInfo(
    val id: String,
    val name: String,
    val level: String,
    val pack: String,
    val speech: String,
    val flag: String,
)

val LANGUAGES = listOf(
    LanguageInfo("es", "Spanisch", "B1", "Alltag", "es-ES", "🇪🇸"),
    LanguageInfo("fr", "Französisch", "A2", "Reisen", "fr-FR", "🇫🇷"),
    LanguageInfo("en", "Englisch", "B1", "Alltag", "en-GB", "🇬🇧"),
    LanguageInfo("it", "Italienisch", "A1", "Grundwortschatz", "it-IT", "🇮🇹"),
    LanguageInfo("ja", "Japanisch", "A1", "Begrüßung", "ja-JP", "🇯🇵"),
    LanguageInfo("pt", "Portugiesisch", "A2", "Alltag", "pt-PT", "🇵🇹"),
    LanguageInfo("nl", "Niederländisch", "A2", "Alltag", "nl-NL", "🇳🇱"),
    LanguageInfo("sv", "Schwedisch", "A1", "Grundwortschatz", "sv-SE", "🇸🇪"),
    LanguageInfo("pl", "Polnisch", "A1", "Grundwortschatz", "pl-PL", "🇵🇱"),
    LanguageInfo("tr", "Türkisch", "A1", "Alltag", "tr-TR", "🇹🇷"),
    LanguageInfo("ru", "Russisch", "A1", "Grundwortschatz", "ru-RU", "🇷🇺"),
    LanguageInfo("ko", "Koreanisch", "A1", "Begrüßung", "ko-KR", "🇰🇷"),
    LanguageInfo("zh", "Chinesisch", "A1", "Grundwortschatz", "zh-CN", "🇨🇳"),
    LanguageInfo("ar", "Arabisch", "A1", "Grundwortschatz", "ar", "🇸🇦"),
    LanguageInfo("da", "Dänisch", "A1", "Alltag", "da-DK", "🇩🇰"),
    LanguageInfo("no", "Norwegisch", "A1", "Alltag", "nb-NO", "🇳🇴"),
    LanguageInfo("fi", "Finnisch", "A1", "Grundwortschatz", "fi-FI", "🇫🇮"),
    LanguageInfo("el", "Griechisch", "A1", "Grundwortschatz", "el-GR", "🇬🇷"),
    LanguageInfo("cs", "Tschechisch", "A1", "Alltag", "cs-CZ", "🇨🇿"),
    LanguageInfo("hu", "Ungarisch", "A1", "Alltag", "hu-HU", "🇭🇺"),
)

fun languageById(id: String): LanguageInfo =
    LANGUAGES.firstOrNull { it.id == id } ?: LANGUAGES.first()

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val lang: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
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
    val source: String = "seed",
    val chapterId: String = "",
)

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val langId: String,
    val title: String,
    val words: Int,
    val accuracy: Int,
    val date: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "daily")
data class DailyEntity(
    @PrimaryKey val date: String,
    val count: Int,
)

data class UserState(
    val onboardingDone: Boolean = false,
    val name: String = "",
    val selectedLang: String = "es",
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
    val cyloneRefreshToken: String = "",
) {
    val isCyloneLinked: Boolean get() = cyloneSub.isNotBlank() || cyloneEmail.isNotBlank()
    val selectedLanguages: List<LanguageInfo>
        get() = selectedLangIds.mapNotNull { id -> LANGUAGES.firstOrNull { it.id == id } }
}

data class VocabPair(
    val word: String,
    val translation: String,
)

data class CyloneProfile(
    val sub: String,
    val name: String,
    val email: String,
    val accessToken: String = "",
    val refreshToken: String = "",
)
