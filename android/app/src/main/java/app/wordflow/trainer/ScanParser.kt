package app.wordflow.trainer

object ScanParser {
    val presets = listOf(" - ", " – ", " — ", "=", " = ", ";", ",", "/", "\t", "|")

    fun parse(text: String, separator: String): List<VocabPair> {
        if (separator.isEmpty()) return emptyList()
        return text.lineSequence()
            .map { it.trim().trimStart('•', '-', '*', '·').trim() }
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val idx = line.indexOf(separator)
                if (idx <= 0) return@mapNotNull null
                val left = line.substring(0, idx).trim()
                val right = line.substring(idx + separator.length).trim()
                if (left.isBlank() || right.isBlank()) null
                else VocabPair(left, right)
            }
            .distinctBy { it.word.lowercase() to it.translation.lowercase() }
            .toList()
    }

    fun bestSeparator(text: String): String {
        return presets.maxBy { parse(text, it).size }
    }
}
