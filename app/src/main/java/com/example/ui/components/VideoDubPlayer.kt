package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DubProject
import com.example.model.SubtitleDialogue
import com.example.ui.SubtitleDisplayMode
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.SubtitleHiColor
import com.example.ui.theme.SubtitleHinglishColor
import com.example.ui.theme.SubtitleJpColor
import com.example.ui.theme.WaveformGreen
import kotlin.math.sin

@Composable
fun VideoDubPlayer(
    project: DubProject,
    playbackPositionMs: Long,
    isPlaying: Boolean,
    activeDialogue: SubtitleDialogue?,
    isSpeakingDub: Boolean,
    subtitleMode: SubtitleDisplayMode,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMs = project.durationMs.coerceAtLeast(1000L)
    val progress = (playbackPositionMs.toFloat() / durationMs).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("video_player_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Video Canvas (16:9 Aspect Ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF0A0F1D))
                    .clickable { onTogglePlay() }
            ) {
                // Procedural cinematic anime scene canvas
                CinematicSceneCanvas(
                    playbackMs = playbackPositionMs,
                    bgStyle = project.bgVideoStyle,
                    isSpeaking = isSpeakingDub || (activeDialogue != null && isPlaying),
                    activeSpeaker = activeDialogue?.speaker ?: ""
                )

                // Top Status Badges: Dubbing Status & Lip-Sync Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category & Scene badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isPlaying) WaveformGreen else Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JP ➔ HI DUB STUDIO",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Audio Voice Sync Active Badge
                    AnimatedVisibility(
                        visible = isSpeakingDub,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SaffronGold.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Voice Cloning Active",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VOICE CLONE DUB ACTIVE",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Subtitle Overlay (Bottom of Video Canvas)
                if (activeDialogue != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Black.copy(alpha = 0.92f)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Speaker tag
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = IndigoLight.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, IndigoLight)
                            ) {
                                Text(
                                    text = "${activeDialogue.speaker} • ${activeDialogue.voiceTone.name.replace("_", " ")}",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Japanese Text (Kanji / Kana)
                            if (subtitleMode == SubtitleDisplayMode.DUAL_JP_HI || subtitleMode == SubtitleDisplayMode.JAPANESE_ONLY) {
                                Text(
                                    text = activeDialogue.japaneseText,
                                    color = SubtitleJpColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 17.sp
                                )
                                Text(
                                    text = activeDialogue.romajiText,
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 10.5.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Hindi Dubbed Text (Devanagari)
                            if (subtitleMode != SubtitleDisplayMode.JAPANESE_ONLY) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = activeDialogue.hindiText,
                                    color = SubtitleHiColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 21.sp
                                )

                                if (subtitleMode == SubtitleDisplayMode.WITH_HINGLISH && activeDialogue.hinglishText.isNotBlank()) {
                                    Text(
                                        text = activeDialogue.hinglishText,
                                        color = SubtitleHinglishColor,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Play / Pause Center Floating Button (faintly visible when paused)
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(54.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Player Timeline & Controls Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF101728))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Seek Bar Slider
                var sliderSeeking by remember { mutableStateOf(false) }
                var tempSliderPos by remember { mutableFloatStateOf(0f) }

                Slider(
                    value = if (sliderSeeking) tempSliderPos else progress,
                    onValueChange = {
                        sliderSeeking = true
                        tempSliderPos = it
                    },
                    onValueChangeFinished = {
                        sliderSeeking = false
                        onSeek((tempSliderPos * durationMs).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronGold,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = Color(0xFF23314D)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .testTag("video_seek_slider")
                )

                // Timestamp & Transport Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Playback time indicator
                    Text(
                        text = "${formatTime(playbackPositionMs)} / ${formatTime(durationMs)}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )

                    // Quick buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onSeek(0L) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Restart",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier
                                .size(40.dp)
                                .background(IndigoLight, CircleShape)
                                .testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CinematicSceneCanvas(
    playbackMs: Long,
    bgStyle: String,
    isSpeaking: Boolean,
    activeSpeaker: String
) {
    val phase = (playbackMs % 4000L) / 4000f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Scene Background Gradient
        val bgBrush = when (bgStyle) {
            "SHONEN_DUEL" -> Brush.verticalGradient(
                colors = listOf(Color(0xFF1E112A), Color(0xFF2D1525), Color(0xFF0F0814))
            )
            "CYBERPUNK_CAFE" -> Brush.verticalGradient(
                colors = listOf(Color(0xFF08182B), Color(0xFF112A45), Color(0xFF080C14))
            )
            else -> Brush.verticalGradient(
                colors = listOf(Color(0xFF0F2618), Color(0xFF163824), Color(0xFF09140C))
            )
        }
        drawRect(brush = bgBrush)

        // 2. Animated Background Particles & Glows
        when (bgStyle) {
            "SHONEN_DUEL" -> {
                // Fiery embers & moon
                drawCircle(
                    color = SakuraPink.copy(alpha = 0.2f),
                    center = Offset(w * 0.8f, h * 0.25f),
                    radius = h * 0.2f
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    center = Offset(w * 0.8f, h * 0.25f),
                    radius = h * 0.12f
                )

                // Embers rising
                for (i in 0..7) {
                    val emberX = (w * (0.1f + i * 0.12f + sin((playbackMs + i * 500) / 400.0) * 0.05f)).toFloat()
                    val emberY = h - ((playbackMs * 0.15f + i * 60) % h)
                    drawCircle(
                        color = if (i % 2 == 0) SaffronGold else SakuraPink,
                        center = Offset(emberX, emberY),
                        radius = 2.5f + (i % 3)
                    )
                }
            }
            "CYBERPUNK_CAFE" -> {
                // Neon city skyline silhouettes & rain lines
                drawRect(
                    color = Color(0xFF060B14),
                    topLeft = Offset(0f, h * 0.5f),
                    size = Size(w, h * 0.5f)
                )
                // Rain streaks
                for (i in 0..12) {
                    val rx = (w * (i * 0.08f + (playbackMs % 300) / 3000f))
                    val ry = ((playbackMs * 0.6f + i * 80) % h)
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.35f),
                        start = Offset(rx, ry),
                        end = Offset(rx - 8f, ry + 24f),
                        strokeWidth = 1.5f
                    )
                }
            }
            else -> {
                // Bamboo grove silhouetted stalks
                for (i in 0..6) {
                    val bx = w * (0.05f + i * 0.16f)
                    drawRect(
                        color = Color(0xFF081C10),
                        topLeft = Offset(bx, 0f),
                        size = Size(20f, h)
                    )
                }
            }
        }

        // 3. Anime Character Silhouette with Animated Talking Mouth (Lip-Sync Visualizer)
        val charCenterX = w * 0.46f
        val charCenterY = h * 0.52f

        // Shoulders / Torso
        val torsoPath = Path().apply {
            moveTo(charCenterX - 110f, h)
            lineTo(charCenterX - 70f, charCenterY + 70f)
            lineTo(charCenterX + 70f, charCenterY + 70f)
            lineTo(charCenterX + 110f, h)
            close()
        }
        drawPath(torsoPath, color = Color(0xFF1E2638))

        // Head
        drawCircle(
            color = Color(0xFF2A364F),
            center = Offset(charCenterX, charCenterY),
            radius = 65f
        )

        // Anime Hairstyle silhouette
        val hairPath = Path().apply {
            moveTo(charCenterX - 75f, charCenterY)
            lineTo(charCenterX - 85f, charCenterY - 45f)
            lineTo(charCenterX - 50f, charCenterY - 75f)
            lineTo(charCenterX - 20f, charCenterY - 95f)
            lineTo(charCenterX + 15f, charCenterY - 75f)
            lineTo(charCenterX + 60f, charCenterY - 85f)
            lineTo(charCenterX + 75f, charCenterY - 20f)
            lineTo(charCenterX + 65f, charCenterY + 20f)
            lineTo(charCenterX + 50f, charCenterY - 30f)
            lineTo(charCenterX - 10f, charCenterY - 40f)
            lineTo(charCenterX - 50f, charCenterY - 20f)
            close()
        }
        drawPath(hairPath, color = Color(0xFF0F1728))

        // Eyes (Anime Sharp Expression)
        val eyeColor = if (isSpeaking) NeonCyan else Color.White
        // Left eye
        drawLine(
            color = eyeColor,
            start = Offset(charCenterX - 38f, charCenterY - 8f),
            end = Offset(charCenterX - 16f, charCenterY - 4f),
            strokeWidth = 4f
        )
        // Right eye
        drawLine(
            color = eyeColor,
            start = Offset(charCenterX + 16f, charCenterY - 4f),
            end = Offset(charCenterX + 38f, charCenterY - 8f),
            strokeWidth = 4f
        )

        // Lip Sync Mouth Animation: Moves organically when dialogue is speaking!
        val mouthY = charCenterY + 28f
        if (isSpeaking) {
            val mouthOpen = (10f + 16f * (0.5f + 0.5f * sin(playbackMs / 70.0).toFloat()))
            // Open talking mouth
            drawOval(
                color = SakuraPink,
                topLeft = Offset(charCenterX - 14f, mouthY - mouthOpen / 2),
                size = Size(28f, mouthOpen)
            )
            // Teeth line
            drawLine(
                color = Color.White,
                start = Offset(charCenterX - 10f, mouthY - 2f),
                end = Offset(charCenterX + 10f, mouthY - 2f),
                strokeWidth = 2.5f
            )
        } else {
            // Closed mouth smirk
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(charCenterX - 12f, mouthY),
                end = Offset(charCenterX + 12f, mouthY),
                strokeWidth = 3f
            )
        }

        // 4. Subtle Audio Waveform overlay on bottom left indicating live track status
        val waveBaseY = h - 22f
        val waveStartX = 18f
        for (i in 0..14) {
            val barAmp = if (isSpeaking) {
                12f * (0.3f + 0.7f * sin((playbackMs + i * 200) / 100.0).toFloat().coerceAtLeast(0.1f))
            } else {
                3f
            }
            drawLine(
                color = if (isSpeaking) SaffronGold else Color.Gray.copy(alpha = 0.5f),
                start = Offset(waveStartX + i * 6f, waveBaseY),
                end = Offset(waveStartX + i * 6f, waveBaseY - barAmp),
                strokeWidth = 3f
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val m = totalSec / 60
    val s = totalSec % 60
    return String.format("%02d:%02d", m, s)
}
