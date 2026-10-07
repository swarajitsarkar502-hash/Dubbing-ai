package com.example.model

data class SubtitleDialogue(
    val id: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val speaker: String,
    val speakerRole: String,
    val japaneseText: String,
    val romajiText: String,
    val hindiText: String,
    val hinglishText: String,
    val voiceTone: VoiceTone = VoiceTone.HEROIC,
    val syncOffsetMs: Int = 0,         // Frame-level offset for lip sync
    val speechRate: Float = 1.0f,       // Time-stretching to match mouth timing
    val isDubbed: Boolean = true
) {
    val durationMs: Long get() = (endTimeMs - startTimeMs).coerceAtLeast(500L)
    
    val effectiveStartMs: Long get() = (startTimeMs + syncOffsetMs).coerceAtLeast(0L)
    val effectiveEndMs: Long get() = (endTimeMs + syncOffsetMs).coerceAtLeast(effectiveStartMs + 500L)

    fun isActiveAt(currentMs: Long): Boolean {
        return currentMs in effectiveStartMs..effectiveEndMs
    }
}
