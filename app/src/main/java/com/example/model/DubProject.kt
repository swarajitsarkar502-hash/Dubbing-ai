package com.example.model

enum class ProjectCategory {
    SHONEN_ACTION,
    SLICE_OF_LIFE,
    CYBERPUNK_SCI_FI,
    SAMURAI_HISTORICAL,
    CUSTOM_IMPORTED
}

data class DubProject(
    val id: String,
    val title: String,
    val japaneseTitle: String,
    val hindiTitle: String,
    val category: ProjectCategory,
    val durationMs: Long,
    val fps: Int = 24,
    val videoUri: String? = null,
    val bgVideoStyle: String = "SHONEN_DUEL", // For procedural animated video canvas
    val dialogues: List<SubtitleDialogue>,
    val selectedVoiceProfileId: String,
    val masterSyncOffsetMs: Int = 0,
    val originalAudioVolume: Float = 0.25f, // Ducked original audio for cinematic dub feel
    val dubbedAudioVolume: Float = 1.0f,    // Clear Hindi voice clone dub
    val masterPlaybackSpeed: Float = 1.0f,
    val isAutoSyncEnabled: Boolean = true
)
