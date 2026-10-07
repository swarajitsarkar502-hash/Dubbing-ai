package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.SubtitleDialogue
import com.example.model.VoiceCloneProfile
import com.example.model.VoiceTone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class DubAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    // Synthesized background audio track for simulating original Japanese audio track & ambient sound
    private var ambientTrack: AudioTrack? = null
    private var ambientJob: Job? = null
    private var originalTrackVolume: Float = 0.25f
    private var dubbedTrackVolume: Float = 1.0f

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("DubAudioEngine", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindiLocale = Locale.forLanguageTag("hi-IN")
            val result = tts?.setLanguage(hindiLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to general Hindi or default
                val altHindi = Locale.forLanguageTag("hi")
                tts?.setLanguage(altHindi)
            }
            isTtsReady = true
        }
    }

    fun setVolumes(originalVolume: Float, dubbedVolume: Float) {
        originalTrackVolume = originalVolume.coerceIn(0f, 1f)
        dubbedTrackVolume = dubbedVolume.coerceIn(0f, 1f)
        ambientTrack?.setVolume(originalTrackVolume)
    }

    fun speakDialogue(
        dialogue: SubtitleDialogue,
        profile: VoiceCloneProfile,
        onStart: () -> Unit = {},
        onDone: () -> Unit = {}
    ) {
        if (!isTtsReady || tts == null || dubbedTrackVolume <= 0.01f) return

        // Compute modified pitch and rate from profile + dialogue tone
        val tonePitchFactor = when (dialogue.voiceTone) {
            VoiceTone.HEROIC -> 1.04f
            VoiceTone.INTENSE_SHOUT -> 1.15f
            VoiceTone.CALM_SENSEI -> 0.82f
            VoiceTone.KAWAII_TSUNDERE -> 1.32f
            VoiceTone.DEEP_VILLAIN -> 0.72f
            VoiceTone.WHISPER_EMOTIONAL -> 0.90f
            VoiceTone.ENERGETIC_NARRATOR -> 1.0f
        }

        val toneRateFactor = when (dialogue.voiceTone) {
            VoiceTone.HEROIC -> 1.05f
            VoiceTone.INTENSE_SHOUT -> 1.18f
            VoiceTone.CALM_SENSEI -> 0.88f
            VoiceTone.KAWAII_TSUNDERE -> 1.10f
            VoiceTone.DEEP_VILLAIN -> 0.92f
            VoiceTone.WHISPER_EMOTIONAL -> 0.80f
            VoiceTone.ENERGETIC_NARRATOR -> 1.0f
        }

        val finalPitch = (profile.pitch * tonePitchFactor).coerceIn(0.5f, 2.0f)
        val finalRate = (profile.speechRate * toneRateFactor * dialogue.speechRate).coerceIn(0.6f, 1.8f)

        tts?.setPitch(finalPitch)
        tts?.setSpeechRate(finalRate)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onStart()
            }

            override fun onDone(utteranceId: String?) {
                onDone()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onDone()
            }
        })

        val utteranceId = "dialogue_${dialogue.id}"
        tts?.speak(
            dialogue.hindiText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId
        )
    }

    fun speakText(
        text: String,
        profile: VoiceCloneProfile,
        onDone: () -> Unit = {}
    ) {
        if (!isTtsReady || tts == null) {
            onDone()
            return
        }

        tts?.setPitch(profile.pitch.coerceIn(0.5f, 2.0f))
        tts?.setSpeechRate(profile.speechRate.coerceIn(0.6f, 1.8f))

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onDone() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { onDone() }
        })

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sample_${System.currentTimeMillis()}")
    }

    fun startAmbientAudio() {
        if (ambientJob?.isActive == true) return

        ambientJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.setVolume(originalTrackVolume)
                track.play()
                ambientTrack = track

                val buffer = ShortArray(bufferSize / 2)
                var phase1 = 0.0
                var phase2 = 0.0

                while (isActive) {
                    if (originalTrackVolume <= 0.01f) {
                        track.setVolume(0f)
                        delay(100)
                        continue
                    } else {
                        track.setVolume(originalTrackVolume)
                    }

                    // Gentle cinematic synth drone to represent original anime soundbed
                    val freq1 = 110.0 // A2 low drone
                    val freq2 = 164.81 // E3 fifth
                    val incr1 = 2.0 * Math.PI * freq1 / sampleRate
                    val incr2 = 2.0 * Math.PI * freq2 / sampleRate

                    for (i in buffer.indices) {
                        val s1 = sin(phase1) * 0.4
                        val s2 = sin(phase2) * 0.2
                        buffer[i] = ((s1 + s2) * 8000).toInt().toShort()
                        phase1 = (phase1 + incr1) % (2.0 * Math.PI)
                        phase2 = (phase2 + incr2) % (2.0 * Math.PI)
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("DubAudioEngine", "Ambient audio stopped", e)
            }
        }
    }

    fun pauseAmbientAudio() {
        ambientJob?.cancel()
        ambientJob = null
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
        } catch (_: Exception) {}
    }

    fun stop() {
        pauseAmbientAudio()
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun release() {
        stop()
        try {
            ambientTrack?.release()
            ambientTrack = null
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }
}
