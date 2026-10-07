package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DubProject
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

@Composable
fun ProjectLibraryScreen(
    viewModel: DubViewModel,
    onProjectSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var exportedContent by remember { mutableStateOf<String?>(null) }
    var exportFormatTitle by remember { mutableStateOf("") }

    // Android Photo Picker for video selection
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.importCustomProject("User Video Dub", uri.toString())
            Toast.makeText(context, "Imported video successfully!", Toast.LENGTH_SHORT).show()
            onProjectSelected()
        }
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
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "Project Library",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Projects & Export Studio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select Japanese anime scenes, import device videos, or export synchronized Hindi subtitles and dub audio mix.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Section 1: Preloaded Anime Scenes & Import
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anime Scene Catalog",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                // Pick Local Video Button (Zero-permission Android Photo Picker)
                Button(
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoLight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("import_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Import Video",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Import Video",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(uiState.projects) { proj ->
            val isCurrent = proj.id == uiState.currentProject.id
            ProjectCardItem(
                project = proj,
                isCurrent = isCurrent,
                onClick = {
                    viewModel.selectProject(proj.id)
                    onProjectSelected()
                }
            )
        }

        // Section 2: Export Studio (SRT / WebVTT / Audio Dub Configuration)
        item {
            Text(
                text = "Export Subtitles & Audio Mix",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_studio_card"),
                colors = CardDefaults.cardColors(containerColor = StudioCardElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Synchronized Subtitle Formats",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Export timestamped Hindi + Japanese subtitles ready for media players & YouTube.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val srt = viewModel.generateSrtContent()
                                exportedContent = srt
                                exportFormatTitle = "SubRip (.SRT) Format"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_srt_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "SRT",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export .SRT", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val vtt = viewModel.generateVttContent()
                                exportedContent = vtt
                                exportFormatTitle = "WebVTT (.VTT) Format"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_vtt_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "VTT",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export .VTT", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Display exported script content box
                    if (exportedContent != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A0F1D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = exportFormatTitle,
                                        color = SaffronGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Subtitles", exportedContent)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied subtitles to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = exportedContent ?: "",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 8
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Gemini AI Status & Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_api_status_card"),
                colors = CardDefaults.cardColors(containerColor = StudioCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (viewModel.isGeminiKeyReady) WaveformGreen.copy(alpha = 0.2f)
                                else Color(0xFF334155),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Gemini Key",
                            tint = if (viewModel.isGeminiKeyReady) WaveformGreen else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (viewModel.isGeminiKeyReady) "Gemini 2.5 Flash AI Active" else "Gemini Lip-Sync Service Ready",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (viewModel.isGeminiKeyReady)
                                "Real-time Japanese-to-Hindi syllable optimization enabled"
                            else
                                "Configurable in Secrets panel for enhanced AI Lip-Sync translation",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: DubProject,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_item_${project.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF1E263D) else StudioCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) SaffronGold else StudioBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = Color.White,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = project.japaneseTitle,
                    color = SakuraPink,
                    fontSize = 11.5.sp
                )
                Text(
                    text = project.hindiTitle,
                    color = SaffronGold,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${project.dialogues.size} Dialogue Lines • ${project.durationMs / 1000}s Duration",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isCurrent) SaffronGold else IndigoLight.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) SaffronGold else IndigoLight)
            ) {
                Text(
                    text = if (isCurrent) "Active ➔" else "Open",
                    color = if (isCurrent) Color.Black else IndigoLight,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
