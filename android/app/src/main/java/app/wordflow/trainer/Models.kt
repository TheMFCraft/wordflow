package app.wordflow.trainer

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LanguageInfo(
    val id: String,
    val name: String,
    val level: String,
    val pack: String,
    val speech: String,
    val free: Boolean,
    val flag: String,
)

val LANGUAGES = listOf(
    LanguageInfo("es", "Spanisch", "B1", "Alltag", "es-ES", true, "🇪🇸"),
    LanguageInfo("fr", "Französisch", "A2", "Reisen", "fr-FR", true, "🇫🇷"),
    LanguageInfo("ja", "Japanisch", "A1", "Begrüßung", "ja-JP", false, "🇯🇵"),
    LanguageInfo("it", "Italienisch", "A1", "Grundwortschatz", "it-IT", false, "🇮🇹"),
    LanguageInfo("en", "Englisch", "B1", "Alltag", "en-GB", false, "🇬🇧"),
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
    val dailyGoal: Int = 24,
    val isPro: Boolean = false,
    val streak: Int = 0,
    val lastStudyDate: String? = null,
    val todayDate: String = "",
    val todayCount: Int = 0,
    val todayCorrect: Int = 0,
    val todayWrong: Int = 0,
    val totalCorrect: Int = 0,
    val totalAttempts: Int = 0,
    val totalSeconds: Long = 0,
)

data class VocabPair(
    val word: String,
    val translation: String,
)
