package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DubProject
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.WaveformGreen
import kotlin.math.sin

@Composable
fun WaveformSyncTimeline(
    project: DubProject,
    playbackPositionMs: Long,
    masterSyncOffsetMs: Int,
    originalVolume: Float,
    dubbedVolume: Float,
    isAiLoading: Boolean,
    onOffsetChanged: (Int) -> Unit,
    onVolumesChanged: (Float, Float) -> Unit,
    onAutoSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("waveform_sync_timeline"),
        colors = CardDefaults.cardColors(containerColor = StudioCardElevated),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Audio Sync",
                        tint = SaffronGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real-Time Audio Sync Timeline",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // AI Auto-Sync Align Button
                Button(
                    onClick = onAutoSyncClick,
                    enabled = !isAiLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoLight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("auto_sync_align_button")
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto Align",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-Sync",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Audio Waveform Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(DeepSpace, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                DualTrackWaveformCanvas(
                    project = project,
                    playbackPositionMs = playbackPositionMs,
                    syncOffsetMs = masterSyncOffsetMs
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lip-Sync Frame Offset Adjustment Slider (-500ms to +500ms)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dub Lip-Sync Delay",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${if (masterSyncOffsetMs > 0) "+" else ""}$masterSyncOffsetMs ms",
                    color = if (masterSyncOffsetMs == 0) WaveformGreen else SaffronGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onOffsetChanged(masterSyncOffsetMs - 50) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "-50ms",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = masterSyncOffsetMs.toFloat(),
                    onValueChange = { onOffsetChanged(it.toInt()) },
                    valueRange = -500f..500f,
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronGold,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sync_offset_slider")
                )

                IconButton(
                    onClick = { onOffsetChanged(masterSyncOffsetMs + 50) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "+50ms",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Track Volume Mixers (Original Japanese vs Hindi Dub)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Original JP Track Volume
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Original JP Audio",
                            color = SakuraPink,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(originalVolume * 100).toInt()}%",
                            color = SakuraPink,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = originalVolume,
                        onValueChange = { onVolumesChanged(it, dubbedVolume) },
                        colors = SliderDefaults.colors(
                            thumbColor = SakuraPink,
                            activeTrackColor = SakuraPink,
                            inactiveTrackColor = Color(0xFF33202A)
                        ),
                        modifier = Modifier.testTag("jp_volume_slider")
                    )
                }

                // Hindi Cloned Dub Volume
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hindi Dub Volume",
                            color = SaffronGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(dubbedVolume * 100).toInt()}%",
                            color = SaffronGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = dubbedVolume,
                        onValueChange = { onVolumesChanged(originalVolume, it) },
                        colors = SliderDefaults.colors(
                            thumbColor = SaffronGold,
                            activeTrackColor = SaffronGold,
                            inactiveTrackColor = Color(0xFF3B2E18)
                        ),
                        modifier = Modifier.testTag("hindi_volume_slider")
                    )
                }
            }
        }
    }
}

@Composable
private fun DualTrackWaveformCanvas(
    project: DubProject,
    playbackPositionMs: Long,
    syncOffsetMs: Int
) {
    Canvas(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
        val w = size.width
        val h = size.height
        val duration = project.durationMs.coerceAtLeast(1000L).toFloat()

        val halfH = h / 2f

        // Track 1 divider and labels
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(0f, halfH),
            end = Offset(w, halfH),
            strokeWidth = 1f
        )

        // Draw Japanese Original Dialogue segments (Top half)
        project.dialogues.forEach { d ->
            val startX = (d.startTimeMs / duration) * w
            val endX = (d.endTimeMs / duration) * w
            val width = (endX - startX).coerceAtLeast(4f)

            // Japanese block highlight
            drawRect(
                color = SakuraPink.copy(alpha = 0.25f),
                topLeft = Offset(startX, 4f),
                size = Size(width, halfH - 8f)
            )

            // Simulated speech waveform bars in JP slot
            val barCount = (width / 5f).toInt().coerceIn(3, 40)
            for (i in 0 until barCount) {
                val bx = startX + i * 5f
                val amp = 10f * (0.3f + 0.7f * sin((d.startTimeMs + i * 80).toDouble()).toFloat().coerceAtLeast(0.1f))
                drawLine(
                    color = SakuraPink,
                    start = Offset(bx, halfH / 2f - amp),
                    end = Offset(bx, halfH / 2f + amp),
                    strokeWidth = 2f
                )
            }
        }

        // Draw Hindi Dubbed Dialogue segments with Sync Offset (Bottom half)
        project.dialogues.forEach { d ->
            val shiftedStart = (d.effectiveStartMs + syncOffsetMs).coerceAtLeast(0L)
            val shiftedEnd = (d.effectiveEndMs + syncOffsetMs).coerceAtLeast(shiftedStart + 500L)

            val startX = (shiftedStart / duration) * w
            val endX = (shiftedEnd / duration) * w
            val width = (endX - startX).coerceAtLeast(4f)

            // Hindi block highlight
            drawRect(
                color = SaffronGold.copy(alpha = 0.25f),
                topLeft = Offset(startX, halfH + 4f),
                size = Size(width, halfH - 8f)
            )

            // Hindi speech waveform bars
            val barCount = (width / 5f).toInt().coerceIn(3, 40)
            for (i in 0 until barCount) {
                val bx = startX + i * 5f
                val amp = 11f * (0.3f + 0.7f * sin((shiftedStart + i * 110).toDouble()).toFloat().coerceAtLeast(0.1f))
                drawLine(
                    color = SaffronGold,
                    start = Offset(bx, halfH + halfH / 2f - amp),
                    end = Offset(bx, halfH + halfH / 2f + amp),
                    strokeWidth = 2f
                )
            }
        }

        // Playhead Scrubber Needle
        val needleX = (playbackPositionMs.toFloat() / duration) * w
        drawLine(
            color = NeonCyan,
            start = Offset(needleX, 0f),
            end = Offset(needleX, h),
            strokeWidth = 2.5f
        )
        drawCircle(
            color = NeonCyan,
            center = Offset(needleX, 0f),
            radius = 4f
        )
        drawCircle(
            color = NeonCyan,
            center = Offset(needleX, h),
            radius = 4f
        )
    }
}
