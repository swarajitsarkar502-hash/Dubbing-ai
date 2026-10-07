package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.VoiceCloneProfile
import com.example.model.VoiceGender
import com.example.ui.DubViewModel
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.WaveformGreen
import kotlin.math.sin

@Composable
fun VoiceCloneLabScreen(
    viewModel: DubViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val recorder = viewModel.voiceCloneRecorder
    val isRecording by recorder.isRecording.collectAsState()
    val recordProgress by recorder.recordingProgressSec.collectAsState()
    val liveAmp by recorder.liveAmplitude.collectAsState()

    // Permission launcher for RECORD_AUDIO
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        recorder.startRecording(maxSeconds = 5) { trainedProfile ->
            viewModel.applyTrainedVoiceProfile(trainedProfile)
        }
    }

    var testPhrase by remember {
        mutableStateOf("नमस्ते! यह मेरी खुद की क्लोन की हुई हिंदी डबिंग आवाज़ है।")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SettingsVoice,
                        contentDescription = "Voice Cloning",
                        tint = SaffronGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voice Clone Studio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Clone your voice or select iconic anime character timbres for Hindi dubbing.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.5.sp
                )
            }
        }

        // Section 1: Live Microphone Voice Cloning Studio Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_clone_recorder_card"),
                colors = CardDefaults.cardColors(containerColor = StudioCardElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isRecording) WaveformGreen else StudioBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Train Your Voice Clone",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Record a 5-second sample to calibrate Hindi timbre",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.5.sp
                            )
                        }

                        if (uiState.customVoiceTrained) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WaveformGreen.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WaveformGreen)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Trained",
                                        tint = WaveformGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CALIBRATED",
                                        color = WaveformGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Guided Sample Prompt to read
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F1524),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Read this Hindi line into mic:",
                                color = SaffronGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"मैं कभी हार नहीं मानूंगा! यह मेरा संकल्प है, और मैं अपने दोस्तों की रक्षा करूंगा!\"",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Waveform Visualizer Box during recording
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .background(Color(0xFF080C16), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LiveMicWaveformCanvas(
                            isRecording = isRecording,
                            amplitude = liveAmp
                        )
                    }

                    if (isRecording) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Analyzing acoustic pitch & resonance...",
                                color = WaveformGreen,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${recordProgress}s / 5s",
                                color = WaveformGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { recordProgress / 5f },
                            color = WaveformGreen,
                            trackColor = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Record Button CTA
                    Button(
                        onClick = {
                            if (isRecording) {
                                recorder.stopRecording()
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    recorder.startRecording(maxSeconds = 5) { trainedProfile ->
                                        viewModel.applyTrainedVoiceProfile(trainedProfile)
                                    }
                                } else {
                                    recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color(0xFFDC2626) else WaveformGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("record_voice_button")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop" else "Record Voice Sample",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRecording) "Stop Recording ($recordProgress/5s)" else "Start 5s Voice Calibration",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 2: Preset Character Voice Profiles
        item {
            Text(
                text = "Preset Voice Profiles",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.voiceProfiles.size) { idx ->
            val profile = uiState.voiceProfiles[idx]
            val isSelected = profile.id == uiState.selectedVoiceProfileId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_profile_card_${profile.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1E243D) else StudioCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) SaffronGold else StudioBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (profile.isCustomUserClone) WaveformGreen.copy(alpha = 0.2f)
                                        else SakuraPink.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = profile.name,
                                    tint = if (profile.isCustomUserClone) WaveformGreen else SakuraPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = profile.hindiTitle,
                                    color = SaffronGold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        // Confidence badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StudioBorder)
                        ) {
                            Text(
                                text = "Match: ${profile.cloneConfidence}%",
                                color = NeonCyan,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = profile.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Acoustic metrics pills (Pitch, Speed, Warmth)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AcousticTag("Pitch: ${String.format("%.2f", profile.pitch)}x")
                        AcousticTag("Pace: ${String.format("%.2f", profile.speechRate)}x")
                        AcousticTag("Warmth: ${(profile.warmth * 100).toInt()}%")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Test Sample & Select as Active Dub Voice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.testVoiceProfile(profile) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_sample_${profile.id}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Hear Sample",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Audition",
                                color = NeonCyan,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.selectVoiceProfile(profile.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) SaffronGold else IndigoLight,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("select_voice_profile_${profile.id}")
                        ) {
                            Text(
                                text = if (isSelected) "Active Dub Voice ✓" else "Use For Dubbing",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Test Phrase Speech Sandbox
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_sandbox_card"),
                colors = CardDefaults.cardColors(containerColor = StudioCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Test Cloned Voice with Custom Phrase",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = testPhrase,
                        onValueChange = { testPhrase = it },
                        label = { Text("Type any Hindi dialogue to synthesize", color = Color(0xFF94A3B8), fontSize = 11.5.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedContainerColor = Color(0xFF0F1524),
                            unfocusedContainerColor = Color(0xFF0F1524)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.testVoiceProfile(viewModel.selectedVoiceProfile, testPhrase)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("synthesize_test_phrase_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Speak",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Synthesize Cloned Voice",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AcousticTag(label: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF1E293B))
    ) {
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            fontSize = 10.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun LiveMicWaveformCanvas(
    isRecording: Boolean,
    amplitude: Float
) {
    Canvas(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val midY = h / 2f
        val barCount = 38
        val barWidth = w / (barCount * 1.5f)

        for (i in 0 until barCount) {
            val x = i * (barWidth * 1.5f)
            val dynamicFactor = if (isRecording) {
                val wave = sin((i * 0.4f + amplitude * 10f).toDouble()).toFloat().coerceAtLeast(0.1f)
                (h * 0.45f * amplitude * wave).coerceIn(4f, h * 0.48f)
            } else {
                3f
            }

            drawLine(
                color = if (isRecording) WaveformGreen else Color(0xFF334155),
                start = Offset(x, midY - dynamicFactor),
                end = Offset(x, midY + dynamicFactor),
                strokeWidth = barWidth
            )
        }
    }
}
