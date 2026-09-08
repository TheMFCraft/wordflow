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
    val activity: List<ActivityEntity> = emptyList(),
    val daily: List<DailyEntity> = emptyList(),
    val learnedByLang: Map<String, Pair<Int, Int>> = emptyMap(),
    val learnedTotal: Int = 0,
    val session: SessionState? = null,
    val ready: Boolean = false,
)

class WordFlowViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WordFlowApplication).repository
    private val extra = MutableStateFlow<SessionState?>(null)
    private var tts: TextToSpeech? = null

    val ui: StateFlow<UiState> = combine(
        repo.user,
        extra,
        repo.activity,
        repo.daily,
        repo.allWords(),
    ) { user, session, activity, daily, allWords ->
        val words = allWords.filter { it.lang == user.selectedLang }
        val learned = LANGUAGES.associate { lang ->
            val list = allWords.filter { it.lang == lang.id }
            lang.id to (list.count { it.box >= 2 } to list.size)
        }
        UiState(
            user = user,
            words = words,
            activity = activity,
            daily = daily,
            learnedByLang = learned,
            learnedTotal = allWords.count { it.box >= 2 },
            session = session,
            ready = true,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        tts = TextToSpeech(getApplication()) { }
    }

    fun completeOnboarding(name: String, lang: String, goal: Int) {
        viewModelScope.launch { repo.completeOnboarding(name, lang, goal) }
    }

    fun selectLang(id: String) {
        viewModelScope.launch { repo.selectLang(id) }
    }

    fun setPro(enabled: Boolean) {
        viewModelScope.launch { repo.setPro(enabled) }
    }

    fun startSession() {
        viewModelScope.launch {
            val user = repo.userOnce()
            val lang = LANGUAGES.first { it.id == user.selectedLang }
            if (!lang.free && !user.isPro) return@launch
            val words = repo.byLang(lang.id).sortedBy { it.box }.take(12)
            if (words.isEmpty()) return@launch
            extra.value = SessionState(lang = lang, queue = words)
        }
    }

    fun flip() {
        extra.value = extra.value?.copy(flipped = !(extra.value?.flipped ?: false))
    }

    fun grade(knew: Boolean) {
        viewModelScope.launch {
            val session = extra.value ?: return@launch
            val word = session.current ?: return@launch
            val first = session.results[word.id] == null
            repo.grade(word, knew)
            var queue = session.queue
            if (!knew && first) queue = queue + word
            val results = session.results + (word.id to knew)
            val nextIndex = session.index + 1
            if (nextIndex >= queue.size) {
                val known = results.values.count { it }
                repo.logSession(session.lang, known, results.size, (System.currentTimeMillis() - session.startedAt) / 1000)
                extra.value = session.copy(queue = queue, index = nextIndex, results = results, flipped = false, done = true)
            } else {
                extra.value = session.copy(queue = queue, index = nextIndex, results = results, flipped = false)
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

    fun importPairs(lang: String, pairs: List<VocabPair>) {
        viewModelScope.launch { repo.importPairs(lang, pairs) }
    }

    suspend fun recognizeText(bitmap: Bitmap, preferJapanese: Boolean): String {
        val software = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }
        val latin = recognizeWith(software, japanese = false)
        if (!preferJapanese) return latin
        val japanese = recognizeWith(software, japanese = true)
        return japanese.ifBlank { latin }
    }

    private suspend fun recognizeWith(bitmap: Bitmap, japanese: Boolean): String {
        val client = if (japanese) {
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
