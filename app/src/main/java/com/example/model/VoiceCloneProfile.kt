package com.example.model

enum class VoiceGender {
    MALE, FEMALE, NEUTRAL
}

enum class VoiceTone {
    HEROIC,
    INTENSE_SHOUT,
    CALM_SENSEI,
    KAWAII_TSUNDERE,
    DEEP_VILLAIN,
    WHISPER_EMOTIONAL,
    ENERGETIC_NARRATOR
}

data class VoiceCloneProfile(
    val id: String,
    val name: String,
    val hindiTitle: String,
    val description: String,
    val gender: VoiceGender,
    val defaultTone: VoiceTone,
    val pitch: Float = 1.0f,            // 0.5f to 2.0f
    val speechRate: Float = 1.0f,       // 0.7f to 1.5f
    val warmth: Float = 0.5f,           // 0.0 to 1.0
    val resonance: Float = 0.5f,        // 0.0 to 1.0
    val isCustomUserClone: Boolean = false,
    val sampleHindiDialogue: String = "मैं कभी हार नहीं मानूंगा! यह मेरा संकल्प है!",
    val cloneConfidence: Int = 94,
    val recordedDurationSec: Int = 0
)
