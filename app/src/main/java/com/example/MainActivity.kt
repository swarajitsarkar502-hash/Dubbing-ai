package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DubViewModel
import com.example.ui.screens.DubStudioScreen
import com.example.ui.screens.ProjectLibraryScreen
import com.example.ui.screens.SubtitleEditorScreen
import com.example.ui.screens.VoiceCloneLabScreen
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard

enum class DubNavDestination(val titleResId: Int, val icon: ImageVector, val tag: String) {
    STUDIO(R.string.tab_studio, Icons.Default.Movie, "nav_tab_studio"),
    SUBTITLES(R.string.tab_subtitles, Icons.Default.Subtitles, "nav_tab_subtitles"),
    VOICE_CLONE(R.string.tab_voice_clone, Icons.Default.RecordVoiceOver, "nav_tab_voice_clone"),
    EXPORT(R.string.tab_export, Icons.Default.VideoLibrary, "nav_tab_export")
}

class MainActivity : ComponentActivity() {

    private val dubViewModel: DubViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

                // System BackHandler
                BackHandler(enabled = selectedTabIndex != 0) {
                    selectedTabIndex = 0
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepSpace,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(IndigoLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = "DubVani Logo",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "DubVani",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "JP ➔ HI",
                                        color = SaffronGold,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = StudioCard,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = StudioCard,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            DubNavDestination.values().forEachIndexed { index, destination ->
                                val isSelected = selectedTabIndex == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { selectedTabIndex = index },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = stringResource(destination.titleResId),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(destination.titleResId),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = SaffronGold,
                                        indicatorColor = SaffronGold,
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.testTag(destination.tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTabIndex) {
                            0 -> DubStudioScreen(
                                viewModel = dubViewModel,
                                onNavigateToSubtitles = { selectedTabIndex = 1 },
                                onNavigateToVoiceLab = { selectedTabIndex = 2 }
                            )
                            1 -> SubtitleEditorScreen(
                                viewModel = dubViewModel
                            )
                            2 -> VoiceCloneLabScreen(
                                viewModel = dubViewModel
                            )
                            3 -> ProjectLibraryScreen(
                                viewModel = dubViewModel,
                                onProjectSelected = { selectedTabIndex = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}
