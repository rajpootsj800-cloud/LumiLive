package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel

enum class MainTab {
    EXPLORE,
    REELS,
    STORE,
    LEADERBOARD,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumiLiveTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: LiveStreamViewModel = viewModel()
) {
    val activeRoom by viewModel.activeRoom.collectAsState()
    val toastMessage by viewModel.toastNotification.collectAsState()
    var currentTab by remember { mutableStateOf(MainTab.EXPLORE) }
    var isStudioOpen by remember { mutableStateOf(false) }
    var isCameraStudioOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        when {
            activeRoom != null -> {
                // Full Screen Live Stream / Party Room Experience
                LiveStreamScreen(
                    viewModel = viewModel,
                    room = activeRoom!!,
                    onNavigateBack = { viewModel.leaveRoom() }
                )
            }
            isStudioOpen -> {
                // Broadcast Studio
                GoLiveStudioScreen(
                    viewModel = viewModel,
                    onNavigateBack = { isStudioOpen = false }
                )
            }
            isCameraStudioOpen -> {
                // Reels Camera Studio with live filters & recording
                CameraStudioScreen(
                    viewModel = viewModel,
                    onNavigateBack = { isCameraStudioOpen = false },
                    onReelPublished = {
                        isCameraStudioOpen = false
                        currentTab = MainTab.REELS
                    }
                )
            }
            else -> {
                // Main Bottom Nav Shell
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BackgroundDark,
                    bottomBar = {
                        NavigationBar(
                            containerColor = SurfaceDark,
                            tonalElevation = 8.dp,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            NavigationBarItem(
                                selected = currentTab == MainTab.EXPLORE,
                                onClick = { currentTab = MainTab.EXPLORE },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.EXPLORE) Icons.Filled.LiveTv else Icons.Outlined.LiveTv,
                                        contentDescription = "Live"
                                    )
                                },
                                label = { Text("Live", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonMagenta,
                                    selectedTextColor = NeonMagenta,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = CardSurfaceElevated
                                ),
                                modifier = Modifier.testTag("tab_explore")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.REELS,
                                onClick = { currentTab = MainTab.REELS },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.REELS) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                                        contentDescription = "Reels"
                                    )
                                },
                                label = { Text("Reels", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonCyan,
                                    selectedTextColor = NeonCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = CardSurfaceElevated
                                ),
                                modifier = Modifier.testTag("tab_reels")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.STORE,
                                onClick = { currentTab = MainTab.STORE },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.STORE) Icons.Filled.Savings else Icons.Outlined.Savings,
                                        contentDescription = "Coin Store"
                                    )
                                },
                                label = { Text("Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GoldCoin,
                                    selectedTextColor = GoldCoin,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = CardSurfaceElevated
                                ),
                                modifier = Modifier.testTag("tab_store")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.LEADERBOARD,
                                onClick = { currentTab = MainTab.LEADERBOARD },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                        contentDescription = "Rankings"
                                    )
                                },
                                label = { Text("Ranks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonCyan,
                                    selectedTextColor = NeonCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = CardSurfaceElevated
                                ),
                                modifier = Modifier.testTag("tab_leaderboard")
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.PROFILE,
                                onClick = { currentTab = MainTab.PROFILE },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text("Me", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonMagenta,
                                    selectedTextColor = NeonMagenta,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = CardSurfaceElevated
                                ),
                                modifier = Modifier.testTag("tab_profile")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            MainTab.EXPLORE -> {
                                ExploreHomeScreen(
                                    viewModel = viewModel,
                                    onRoomSelected = { room ->
                                        viewModel.joinRoom(room)
                                    },
                                    onGoLiveClick = {
                                        isStudioOpen = true
                                    }
                                )
                            }
                            MainTab.REELS -> {
                                ReelsFeedScreen(
                                    viewModel = viewModel,
                                    onOpenCameraStudio = {
                                        isCameraStudioOpen = true
                                    }
                                )
                            }
                            MainTab.STORE -> {
                                CoinShopScreen(viewModel = viewModel)
                            }
                            MainTab.LEADERBOARD -> {
                                LeaderboardScreen()
                            }
                            MainTab.PROFILE -> {
                                ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }

        // Global Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 60.dp)
        ) { data ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
