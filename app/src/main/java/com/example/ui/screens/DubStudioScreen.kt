package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceCloneProfile
import com.example.ui.DubViewModel
import com.example.ui.SubtitleDisplayMode
import com.example.ui.components.VideoDubPlayer
import com.example.ui.components.WaveformSyncTimeline
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.WaveformGreen

@Composable
fun DubStudioScreen(
    viewModel: DubViewModel,
    onNavigateToSubtitles: () -> Unit,
    onNavigateToVoiceLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Notification Toast Banner
        item {
            AnimatedVisibility(visible = uiState.aiStatusMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E1B4B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoLight)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "AI Status",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.aiStatusMessage ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 1. Primary Video Player
        item {
            VideoDubPlayer(
                project = uiState.currentProject,
                playbackPositionMs = uiState.playbackPositionMs,
                isPlaying = uiState.isPlaying,
                activeDialogue = uiState.activeDialogue,
                isSpeakingDub = uiState.isSpeakingDub,
                subtitleMode = uiState.subtitleMode,
                onTogglePlay = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekTo(it) }
            )
        }

        // 2. Active Voice Clone Profile Selector (Quick Pills)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice Clone",
                            tint = SaffronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Hindi Dubbing Voice",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Customize ➔",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToVoiceLab() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.voiceProfiles.forEach { profile ->
                        val isSelected = profile.id == uiState.selectedVoiceProfileId
                        VoicePillItem(
                            profile = profile,
                            isSelected = isSelected,
                            onClick = { viewModel.selectVoiceProfile(profile.id) }
                        )
                    }
                }
            }
        }

        // 3. Subtitle Display Mode Filter Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Subtitle Mode:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubtitleModeChip(
                        label = "Dual JP + Hindi",
                        selected = uiState.subtitleMode == SubtitleDisplayMode.DUAL_JP_HI,
                        onClick = { viewModel.setSubtitleDisplayMode(SubtitleDisplayMode.DUAL_JP_HI) }
                    )
                    SubtitleModeChip(
                        label = "Hindi + Hinglish",
                        selected = uiState.subtitleMode == SubtitleDisplayMode.WITH_HINGLISH,
                        onClick = { viewModel.setSubtitleDisplayMode(SubtitleDisplayMode.WITH_HINGLISH) }
                    )
                    SubtitleModeChip(
                        label = "Hindi Only",
                        selected = uiState.subtitleMode == SubtitleDisplayMode.HINDI_PRIMARY,
                        onClick = { viewModel.setSubtitleDisplayMode(SubtitleDisplayMode.HINDI_PRIMARY) }
                    )
                    SubtitleModeChip(
                        label = "Japanese Only",
                        selected = uiState.subtitleMode == SubtitleDisplayMode.JAPANESE_ONLY,
                        onClick = { viewModel.setSubtitleDisplayMode(SubtitleDisplayMode.JAPANESE_ONLY) }
                    )
                }
            }
        }

        // 4. Waveform & Audio Sync Timeline Mixer
        item {
            WaveformSyncTimeline(
                project = uiState.currentProject,
                playbackPositionMs = uiState.playbackPositionMs,
                masterSyncOffsetMs = uiState.masterSyncOffsetMs,
                originalVolume = uiState.originalAudioVolume,
                dubbedVolume = uiState.dubbedAudioVolume,
                isAiLoading = uiState.isAiLoading,
                onOffsetChanged = { viewModel.setMasterSyncOffset(it) },
                onVolumesChanged = { orig, dub -> viewModel.setVolumes(orig, dub) },
                onAutoSyncClick = { viewModel.autoAlignLipSync() }
            )
        }

        // 5. Quick Script Card CTA
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSubtitles() }
                    .testTag("open_dialogue_editor_cta"),
                colors = CardDefaults.cardColors(containerColor = StudioCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = "Subtitles",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dialogue Script & Lip-Sync (${uiState.currentProject.dialogues.size} lines)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Edit Hindi translations, syllables & tone tags",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IndigoLight.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoLight)
                    ) {
                        Text(
                            text = "Edit ➔",
                            color = IndigoLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoicePillItem(
    profile: VoiceCloneProfile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SaffronGold else StudioBorder
    val bg = if (isSelected) Color(0xFF382711) else StudioCard

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.testTag("voice_pill_${profile.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        if (profile.isCustomUserClone) WaveformGreen else SakuraPink,
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = profile.name,
                    color = if (isSelected) SaffronGold else Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = profile.hindiTitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = SaffronGold,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SubtitleModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = IndigoLight.copy(alpha = 0.3f),
            selectedLabelColor = Color.White,
            containerColor = StudioCard,
            labelColor = Color(0xFF94A3B8)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) IndigoLight else StudioBorder
        )
    )
}
