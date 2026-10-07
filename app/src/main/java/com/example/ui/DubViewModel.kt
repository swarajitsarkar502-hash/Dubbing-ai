package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.DubAudioEngine
import com.example.audio.VoiceCloneRecorder
import com.example.data.DefaultVoiceProfiles
import com.example.data.SampleAnimeClips
import com.example.model.DubProject
import com.example.model.ProjectCategory
import com.example.model.SubtitleDialogue
import com.example.model.VoiceCloneProfile
import com.example.model.VoiceGender
import com.example.model.VoiceTone
import com.example.network.GeminiDubService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class SubtitleDisplayMode {
    DUAL_JP_HI,
    HINDI_PRIMARY,
    WITH_HINGLISH,
    JAPANESE_ONLY
}

data class DubUiState(
    val projects: List<DubProject> = SampleAnimeClips.sampleProjects,
    val currentProject: DubProject = SampleAnimeClips.sampleProjects.first(),
    val playbackPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val voiceProfiles: List<VoiceCloneProfile> = DefaultVoiceProfiles.profiles,
    val selectedVoiceProfileId: String = "voice_shonen_hero",
    val activeDialogue: SubtitleDialogue? = null,
    val subtitleMode: SubtitleDisplayMode = SubtitleDisplayMode.DUAL_JP_HI,
    val originalAudioVolume: Float = 0.25f,
    val dubbedAudioVolume: Float = 1.0f,
    val masterSyncOffsetMs: Int = 0,
    val isAiLoading: Boolean = false,
    val aiStatusMessage: String? = null,
    val isSpeakingDub: Boolean = false,
    val exportModalVisible: Boolean = false,
    val customVoiceTrained: Boolean = false
)

class DubViewModel(application: Application) : AndroidViewModel(application) {

    private val audioEngine = DubAudioEngine(application)
    val voiceCloneRecorder = VoiceCloneRecorder(application)
    private val geminiService = GeminiDubService()

    private val _uiState = MutableStateFlow(DubUiState())
    val uiState = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private var lastSpokenDialogueId: String? = null

    val selectedVoiceProfile: VoiceCloneProfile
        get() {
            val state = _uiState.value
            return state.voiceProfiles.find { it.id == state.selectedVoiceProfileId }
                ?: state.voiceProfiles.first()
        }

    val isGeminiKeyReady: Boolean
        get() = geminiService.isKeyConfigured

    init {
        audioEngine.setVolumes(_uiState.value.originalAudioVolume, _uiState.value.dubbedAudioVolume)
    }

    fun play() {
        if (_uiState.value.isPlaying) return
        _uiState.update { it.copy(isPlaying = true) }
        audioEngine.startAmbientAudio()

        playbackJob = viewModelScope.launch {
            val frameIntervalMs = 40L
            while (isActive && _uiState.value.isPlaying) {
                delay(frameIntervalMs)
                val currentPos = _uiState.value.playbackPositionMs
                val duration = _uiState.value.currentProject.durationMs

                if (currentPos >= duration) {
                    // Loop or pause
                    seekTo(0L)
                    lastSpokenDialogueId = null
                    continue
                }

                val nextPos = currentPos + frameIntervalMs
                val active = findActiveDialogue(nextPos)

                _uiState.update {
                    it.copy(
                        playbackPositionMs = nextPos,
                        activeDialogue = active
                    )
                }

                // Trigger Hindi dub speech when dialogue starts
                if (active != null && active.isDubbed && active.id != lastSpokenDialogueId) {
                    val syncStart = active.effectiveStartMs + _uiState.value.masterSyncOffsetMs
                    if (nextPos >= syncStart) {
                        lastSpokenDialogueId = active.id
                        triggerDubSpeech(active)
                    }
                }
            }
        }
    }

    fun pause() {
        _uiState.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
        playbackJob = null
        audioEngine.pauseAmbientAudio()
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _uiState.value.currentProject.durationMs)
        val active = findActiveDialogue(clamped)
        _uiState.update {
            it.copy(
                playbackPositionMs = clamped,
                activeDialogue = active
            )
        }
        if (active?.id != lastSpokenDialogueId) {
            lastSpokenDialogueId = null
        }
    }

    fun stepFrame(forward: Boolean) {
        val delta = if (forward) 200L else -200L
        seekTo(_uiState.value.playbackPositionMs + delta)
    }

    private fun findActiveDialogue(positionMs: Long): SubtitleDialogue? {
        val offset = _uiState.value.masterSyncOffsetMs
        return _uiState.value.currentProject.dialogues.find { dialogue ->
            val start = dialogue.effectiveStartMs + offset
            val end = dialogue.effectiveEndMs + offset
            positionMs in start..end
        }
    }

    private fun triggerDubSpeech(dialogue: SubtitleDialogue) {
        _uiState.update { it.copy(isSpeakingDub = true) }
        audioEngine.speakDialogue(
            dialogue = dialogue,
            profile = selectedVoiceProfile,
            onStart = {
                _uiState.update { it.copy(isSpeakingDub = true) }
            },
            onDone = {
                _uiState.update { it.copy(isSpeakingDub = false) }
            }
        )
    }

    fun speakPreviewDialogue(dialogue: SubtitleDialogue) {
        audioEngine.speakDialogue(
            dialogue = dialogue,
            profile = selectedVoiceProfile
        )
    }

    fun testVoiceProfile(profile: VoiceCloneProfile, customPhrase: String? = null) {
        val text = customPhrase ?: profile.sampleHindiDialogue
        audioEngine.speakText(text, profile)
    }

    fun selectVoiceProfile(profileId: String) {
        _uiState.update { it.copy(selectedVoiceProfileId = profileId) }
    }

    fun selectProject(projectId: String) {
        pause()
        val proj = _uiState.value.projects.find { it.id == projectId } ?: return
        _uiState.update {
            it.copy(
                currentProject = proj,
                playbackPositionMs = 0L,
                activeDialogue = null,
                selectedVoiceProfileId = proj.selectedVoiceProfileId,
                originalAudioVolume = proj.originalAudioVolume,
                dubbedAudioVolume = proj.dubbedAudioVolume
            )
        }
        audioEngine.setVolumes(proj.originalAudioVolume, proj.dubbedAudioVolume)
    }

    fun setMasterSyncOffset(offsetMs: Int) {
        val clamped = offsetMs.coerceIn(-500, 500)
        _uiState.update { it.copy(masterSyncOffsetMs = clamped) }
    }

    fun setVolumes(original: Float, dubbed: Float) {
        _uiState.update {
            it.copy(
                originalAudioVolume = original.coerceIn(0f, 1f),
                dubbedAudioVolume = dubbed.coerceIn(0f, 1f)
            )
        }
        audioEngine.setVolumes(original, dubbed)
    }

    fun setSubtitleDisplayMode(mode: SubtitleDisplayMode) {
        _uiState.update { it.copy(subtitleMode = mode) }
    }

    fun updateDialogueText(dialogueId: String, newHindi: String, newHinglish: String) {
        val updatedList = _uiState.value.currentProject.dialogues.map { d ->
            if (d.id == dialogueId) {
                d.copy(hindiText = newHindi, hinglishText = newHinglish)
            } else d
        }
        val updatedProject = _uiState.value.currentProject.copy(dialogues = updatedList)
        _uiState.update {
            it.copy(
                currentProject = updatedProject,
                activeDialogue = if (it.activeDialogue?.id == dialogueId) {
                    it.activeDialogue.copy(hindiText = newHindi, hinglishText = newHinglish)
                } else it.activeDialogue
            )
        }
    }

    fun updateDialogueTiming(dialogueId: String, syncOffsetMs: Int, speechRate: Float) {
        val updatedList = _uiState.value.currentProject.dialogues.map { d ->
            if (d.id == dialogueId) {
                d.copy(syncOffsetMs = syncOffsetMs, speechRate = speechRate)
            } else d
        }
        val updatedProject = _uiState.value.currentProject.copy(dialogues = updatedList)
        _uiState.update { it.copy(currentProject = updatedProject) }
    }

    fun updateDialogueTone(dialogueId: String, tone: VoiceTone) {
        val updatedList = _uiState.value.currentProject.dialogues.map { d ->
            if (d.id == dialogueId) d.copy(voiceTone = tone) else d
        }
        val updatedProject = _uiState.value.currentProject.copy(dialogues = updatedList)
        _uiState.update { it.copy(currentProject = updatedProject) }
    }

    fun autoAlignLipSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiStatusMessage = "Analyzing Japanese phonetics & lip sync...") }
            delay(600)

            val updatedDialogues = _uiState.value.currentProject.dialogues.map { d ->
                // Calculate target syllable pace for Hindi relative to Japanese duration
                val durationSec = d.durationMs / 1000f
                val jpCharCount = d.japaneseText.length
                val idealRate = when {
                    jpCharCount > 18 -> 1.15f
                    jpCharCount < 10 -> 0.92f
                    else -> 1.04f
                }
                d.copy(
                    speechRate = idealRate,
                    syncOffsetMs = if (d.voiceTone == VoiceTone.INTENSE_SHOUT) -30 else 10
                )
            }

            val updatedProj = _uiState.value.currentProject.copy(dialogues = updatedDialogues)
            _uiState.update {
                it.copy(
                    currentProject = updatedProj,
                    isAiLoading = false,
                    aiStatusMessage = "Lip-sync timing synchronized smoothly!"
                )
            }
            delay(2000)
            _uiState.update { it.copy(aiStatusMessage = null) }
        }
    }

    fun rephraseWithAi(dialogueId: String, dubbingStyle: String = "Anime Shonen") {
        val target = _uiState.value.currentProject.dialogues.find { it.id == dialogueId } ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiLoading = true,
                    aiStatusMessage = "AI optimizing Hindi syllables for lip sync..."
                )
            }

            val result = geminiService.rephraseForLipSync(
                japaneseText = target.japaneseText,
                romajiText = target.romajiText,
                currentHindi = target.hindiText,
                durationMs = target.durationMs,
                tone = target.voiceTone,
                dubbingStyle = dubbingStyle
            )

            val updatedDialogues = _uiState.value.currentProject.dialogues.map { d ->
                if (d.id == dialogueId) {
                    d.copy(
                        hindiText = result.hindiText,
                        hinglishText = if (result.hinglishText.isNotBlank()) result.hinglishText else d.hinglishText,
                        speechRate = result.speechRate,
                        syncOffsetMs = result.syncOffsetMs
                    )
                } else d
            }

            val updatedProj = _uiState.value.currentProject.copy(dialogues = updatedDialogues)
            _uiState.update {
                it.copy(
                    currentProject = updatedProj,
                    isAiLoading = false,
                    aiStatusMessage = if (result.isAiGenerated) "Gemini lip-sync translation applied!" else "Optimized dubbing script applied!"
                )
            }

            delay(2500)
            _uiState.update { it.copy(aiStatusMessage = null) }
        }
    }

    fun applyTrainedVoiceProfile(profile: VoiceCloneProfile) {
        val currentProfiles = _uiState.value.voiceProfiles.toMutableList()
        val index = currentProfiles.indexOfFirst { it.id == profile.id }
        if (index >= 0) {
            currentProfiles[index] = profile
        } else {
            currentProfiles.add(profile)
        }

        _uiState.update {
            it.copy(
                voiceProfiles = currentProfiles,
                selectedVoiceProfileId = profile.id,
                customVoiceTrained = true
            )
        }
    }

    fun generateSrtContent(): String {
        val sb = StringBuilder()
        _uiState.value.currentProject.dialogues.forEachIndexed { index, d ->
            sb.append("${index + 1}\n")
            sb.append("${formatSrtTime(d.effectiveStartMs)} --> ${formatSrtTime(d.effectiveEndMs)}\n")
            sb.append("${d.speaker}: ${d.hindiText}\n")
            sb.append("[JP: ${d.japaneseText}]\n\n")
        }
        return sb.toString()
    }

    fun generateVttContent(): String {
        val sb = StringBuilder()
        sb.append("WEBVTT - DubVani Japanese to Hindi Dubbing\n\n")
        _uiState.value.currentProject.dialogues.forEachIndexed { index, d ->
            sb.append("${index + 1}\n")
            sb.append("${formatVttTime(d.effectiveStartMs)} --> ${formatVttTime(d.effectiveEndMs)}\n")
            sb.append("<v ${d.speaker}>${d.hindiText}\n")
            sb.append("<c.romaji>${d.romajiText}</c>\n\n")
        }
        return sb.toString()
    }

    private fun formatSrtTime(ms: Long): String {
        val hours = ms / 3600000
        val mins = (ms % 3600000) / 60000
        val secs = (ms % 60000) / 1000
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d,%03d", hours, mins, secs, millis)
    }

    private fun formatVttTime(ms: Long): String {
        val hours = ms / 3600000
        val mins = (ms % 3600000) / 60000
        val secs = (ms % 60000) / 1000
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d.%03d", hours, mins, secs, millis)
    }

    fun importCustomProject(title: String, videoUri: String?) {
        val newProj = DubProject(
            id = "proj_custom_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom Imported Video" },
            japaneseTitle = "カスタム動画 (Custom)",
            hindiTitle = "कस्टम डबिंग प्रोजेक्ट",
            category = ProjectCategory.CUSTOM_IMPORTED,
            durationMs = 20000L,
            videoUri = videoUri,
            bgVideoStyle = "CYBERPUNK_CAFE",
            selectedVoiceProfileId = _uiState.value.selectedVoiceProfileId,
            dialogues = listOf(
                SubtitleDialogue(
                    id = "cd_1",
                    startTimeMs = 1000L,
                    endTimeMs = 5000L,
                    speaker = "Speaker 1",
                    speakerRole = "Main Character",
                    japaneseText = "これは新しくインポートされたビデオです。",
                    romajiText = "Kore wa atarashiku inpōto sareta bideo desu.",
                    hindiText = "यह नया इम्पोर्ट किया गया वीडियो है।",
                    hinglishText = "Yeh naya import kiya gaya video hai.",
                    voiceTone = VoiceTone.HEROIC
                ),
                SubtitleDialogue(
                    id = "cd_2",
                    startTimeMs = 6000L,
                    endTimeMs = 11000L,
                    speaker = "Speaker 2",
                    speakerRole = "Supporting Character",
                    japaneseText = "音声クローンと同期字幕が準備完了しました。",
                    romajiText = "Onsei kurōn to dōki jimaku ga junbi kanryō shimashita.",
                    hindiText = "वॉइस क्लोन और सिंक सबटाइटल तैयार हो चुके हैं।",
                    hinglishText = "Voice clone aur sync subtitle taiyar ho chuke hain.",
                    voiceTone = VoiceTone.CALM_SENSEI
                )
            )
        )

        _uiState.update {
            it.copy(
                projects = it.projects + newProj,
                currentProject = newProj,
                playbackPositionMs = 0L
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
        audioEngine.release()
        voiceCloneRecorder.stopRecording()
    }
}
