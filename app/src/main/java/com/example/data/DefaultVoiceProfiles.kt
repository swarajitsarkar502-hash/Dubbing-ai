package com.example.data

import com.example.model.VoiceCloneProfile
import com.example.model.VoiceGender
import com.example.model.VoiceTone

object DefaultVoiceProfiles {

    val profiles = listOf(
        VoiceCloneProfile(
            id = "voice_shonen_hero",
            name = "Ryu (Shonen Hero)",
            hindiTitle = "वीर - शोनन हीरो",
            description = "High energy, passionate, heroic punch with fierce Hindi delivery",
            gender = VoiceGender.MALE,
            defaultTone = VoiceTone.HEROIC,
            pitch = 1.05f,
            speechRate = 1.05f,
            warmth = 0.6f,
            resonance = 0.8f,
            sampleHindiDialogue = "चाहे कुछ भी हो जाए, मैं अपने दोस्तों को कभी अकेला नहीं छोड़ूंगा!",
            cloneConfidence = 97
        ),
        VoiceCloneProfile(
            id = "voice_tsundere_heroine",
            name = "Aoi (Kawaii Tsundere)",
            hindiTitle = "अनन्या - त्सुन्देरे आवाज़",
            description = "Slightly sharp, cute, lively anime female voice with expressive cadence",
            gender = VoiceGender.FEMALE,
            defaultTone = VoiceTone.KAWAII_TSUNDERE,
            pitch = 1.35f,
            speechRate = 1.10f,
            warmth = 0.7f,
            resonance = 0.5f,
            sampleHindiDialogue = "बेवकूफ! ऐसा मत सोचो कि मैंने यह तुम्हारे लिए किया है... समझे?!",
            cloneConfidence = 95
        ),
        VoiceCloneProfile(
            id = "voice_calm_sensei",
            name = "Kenshin (Wise Sensei)",
            hindiTitle = "गुरुजी विक्रम - शांत मार्गदर्शक",
            description = "Deep, steady, authoritative master voice with tranquil pauses",
            gender = VoiceGender.MALE,
            defaultTone = VoiceTone.CALM_SENSEI,
            pitch = 0.82f,
            speechRate = 0.88f,
            warmth = 0.85f,
            resonance = 0.9f,
            sampleHindiDialogue = "शांत रहो। तलवार की गति से पहले मन की शांति आवश्यक है।",
            cloneConfidence = 96
        ),
        VoiceCloneProfile(
            id = "voice_dark_villain",
            name = "Kage (Deep Anime Villain)",
            hindiTitle = "साया - गहरा खलनायक",
            description = "Gravelly, chilling, cinematic baritone for antagonist monologues",
            gender = VoiceGender.MALE,
            defaultTone = VoiceTone.DEEP_VILLAIN,
            pitch = 0.70f,
            speechRate = 0.92f,
            warmth = 0.3f,
            resonance = 0.95f,
            sampleHindiDialogue = "तुम्हारा यह प्रकाश... मेरे अनंत अंधकार के सामने कुछ भी नहीं है!",
            cloneConfidence = 94
        ),
        VoiceCloneProfile(
            id = "voice_energetic_narrator",
            name = "Kaito (Studio Narrator)",
            hindiTitle = "रोहन - स्टूडियो नरेटर",
            description = "Crisp, balanced commercial Hindi dubbing announcer",
            gender = VoiceGender.NEUTRAL,
            defaultTone = VoiceTone.ENERGETIC_NARRATOR,
            pitch = 1.0f,
            speechRate = 1.0f,
            warmth = 0.7f,
            resonance = 0.75f,
            sampleHindiDialogue = "और इस तरह टोक्यो की सड़कों पर शुरू हुआ एक नया महासंग्राम...",
            cloneConfidence = 98
        ),
        VoiceCloneProfile(
            id = "voice_user_custom",
            name = "My Cloned Voice (Custom)",
            hindiTitle = "मेरी क्लोन आवाज़ (कस्टम)",
            description = "Trained from your microphone recording sample with calibrated timbre",
            gender = VoiceGender.MALE,
            defaultTone = VoiceTone.HEROIC,
            pitch = 1.0f,
            speechRate = 1.0f,
            warmth = 0.5f,
            resonance = 0.5f,
            isCustomUserClone = true,
            sampleHindiDialogue = "नमस्ते! यह मेरी खुद की क्लोन की हुई हिंदी डबिंग आवाज़ है।",
            cloneConfidence = 91,
            recordedDurationSec = 5
        )
    )

    fun getProfile(id: String): VoiceCloneProfile {
        return profiles.find { it.id == id } ?: profiles.first()
    }
}
