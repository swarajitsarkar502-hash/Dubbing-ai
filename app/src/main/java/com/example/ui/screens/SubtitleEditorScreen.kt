package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubtitleDialogue
import com.example.model.VoiceTone
import com.example.ui.DubViewModel
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.SubtitleJpColor

@Composable
fun SubtitleEditorScreen(
    viewModel: DubViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val project = uiState.currentProject

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Top Header
        Surface(
            color = StudioCardElevated,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Japanese ➔ Hindi Dialogue Editor",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${project.title} • ${project.dialogues.size} Synchronized Dialogue Lines",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // List of dialogue segment cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(project.dialogues) { index, dialogue ->
                DialogueEditCard(
                    index = index + 1,
                    dialogue = dialogue,
                    isAiLoading = uiState.isAiLoading,
                    onUpdateHindi = { newH, newHng ->
                        viewModel.updateDialogueText(dialogue.id, newH, newHng)
                    },
                    onUpdateTone = { newTone ->
                        viewModel.updateDialogueTone(dialogue.id, newTone)
                    },
                    onUpdateTiming = { offset, speed ->
                        viewModel.updateDialogueTiming(dialogue.id, offset, speed)
                    },
                    onAudition = {
                        viewModel.speakPreviewDialogue(dialogue)
                    },
                    onAiRephrase = {
                        viewModel.rephraseWithAi(dialogue.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun DialogueEditCard(
    index: Int,
    dialogue: SubtitleDialogue,
    isAiLoading: Boolean,
    onUpdateHindi: (String, String) -> Unit,
    onUpdateTone: (VoiceTone) -> Unit,
    onUpdateTiming: (Int, Float) -> Unit,
    onAudition: () -> Unit,
    onAiRephrase: () -> Unit
) {
    var hindiText by remember(dialogue.hindiText) { mutableStateOf(dialogue.hindiText) }
    var hinglishText by remember(dialogue.hinglishText) { mutableStateOf(dialogue.hinglishText) }
    var showTimingSliders by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dialogue_card_${dialogue.id}"),
        colors = CardDefaults.cardColors(containerColor = StudioCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header: Dialogue #, Speaker, Timestamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IndigoLight.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "#$index",
                            color = IndigoLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dialogue.speaker,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Timestamp span badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Time",
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatTimestamp(dialogue.startTimeMs)} - ${formatTimestamp(dialogue.endTimeMs)} (${(dialogue.durationMs / 1000f)}s)",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Japanese Source Box (Kanji + Romaji)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF161F33),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SakuraPink.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "日本語",
                                color = SakuraPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dialogue.japaneseText,
                            color = SubtitleJpColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dialogue.romajiText,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hindi Editable Translation Field
            OutlinedTextField(
                value = hindiText,
                onValueChange = {
                    hindiText = it
                    onUpdateHindi(it, hinglishText)
                },
                label = { Text("हिंदी डब डायलॉग (Hindi Dub Dialogue)", color = SaffronGold, fontSize = 11.5.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SaffronGold,
                    unfocusedBorderColor = StudioBorder,
                    focusedContainerColor = Color(0xFF0F1524),
                    unfocusedContainerColor = Color(0xFF0F1524)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hindi_text_field_${dialogue.id}")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hinglish Pronunciation Field
            OutlinedTextField(
                value = hinglishText,
                onValueChange = {
                    hinglishText = it
                    onUpdateHindi(hindiText, it)
                },
                label = { Text("Hinglish Phonics", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFCBD5E1),
                    unfocusedTextColor = Color(0xFFCBD5E1),
                    focusedBorderColor = IndigoLight,
                    unfocusedBorderColor = StudioBorder.copy(alpha = 0.6f),
                    focusedContainerColor = Color(0xFF0A0F1D),
                    unfocusedContainerColor = Color(0xFF0A0F1D)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Voice Tone Chips
            Text(
                text = "Character Voice Tone:",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VoiceTone.values().forEach { tone ->
                    val isToneSelected = dialogue.voiceTone == tone
                    FilterChip(
                        selected = isToneSelected,
                        onClick = { onUpdateTone(tone) },
                        label = {
                            Text(
                                text = tone.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 10.5.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronGold.copy(alpha = 0.3f),
                            selectedLabelColor = SaffronGold,
                            containerColor = Color(0xFF161F33),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isToneSelected,
                            borderColor = if (isToneSelected) SaffronGold else StudioBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: AI Rephrase for Lip-Sync + Audition Speech + Fine-Tune
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gemini AI Lip-Sync Rephrase
                Button(
                    onClick = onAiRephrase,
                    enabled = !isAiLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoLight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_rephrase_button_${dialogue.id}")
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
                            contentDescription = "AI Lip-Sync",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Lip-Sync",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Audition Hindi Voice Line
                Button(
                    onClick = onAudition,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("audition_dialogue_button_${dialogue.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Audition",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Listen",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Timing & Speed toggle
                IconButton(
                    onClick = { showTimingSliders = !showTimingSliders },
                    modifier = Modifier
                        .size(38.dp)
                        .background(StudioCardElevated, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Adjust Timing",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expandable Timing & Syllable Speed Sliders
            AnimatedVisibility(visible = showTimingSliders) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Color(0xFF0F1524), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    // Speech Speed Slider (0.8x to 1.3x)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speech Rate (Fit Japanese Timing)",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = String.format("%.2fx", dialogue.speechRate),
                            color = NeonCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = dialogue.speechRate,
                        onValueChange = { onUpdateTiming(dialogue.syncOffsetMs, it) },
                        valueRange = 0.8f..1.35f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )

                    // Line Sync Offset Slider (-200ms to +200ms)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Line Offset",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "${if (dialogue.syncOffsetMs > 0) "+" else ""}${dialogue.syncOffsetMs} ms",
                            color = SaffronGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = dialogue.syncOffsetMs.toFloat(),
                        onValueChange = { onUpdateTiming(it.toInt(), dialogue.speechRate) },
                        valueRange = -200f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = SaffronGold,
                            activeTrackColor = SaffronGold,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(ms: Long): String {
    val mins = (ms / 60000)
    val secs = (ms % 60000) / 1000
    val millis = (ms % 1000) / 100
    return String.format("%02d:%02d.%d", mins, secs, millis)
}
