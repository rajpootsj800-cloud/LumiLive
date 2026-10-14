package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.MockDataProvider
import com.example.model.CameraFilter
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraStudioScreen(
    viewModel: LiveStreamViewModel,
    onNavigateBack: () -> Unit,
    onReelPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val selectedFilter by viewModel.selectedCameraFilter.collectAsState()

    var isFrontCamera by remember { mutableStateOf(true) }
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var selectedDurationSec by remember { mutableIntStateOf(15) }
    var selectedMusic by remember { mutableStateOf("🎵 Neon Echo • Midnight Pulse") }

    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var isRecordedComplete by remember { mutableStateOf(false) }
    var reelCaption by remember { mutableStateOf("Late night vibes with live filter! ✨🎥") }

    val coroutineScope = rememberCoroutineScope()

    // Recording progress timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingProgress = 0f
            val totalSteps = selectedDurationSec * 10
            for (i in 1..totalSteps) {
                delay(100)
                recordingProgress = i.toFloat() / totalSteps.toFloat()
                if (!isRecording) break
            }
            if (isRecording) {
                isRecording = false
                isRecordedComplete = true
            }
        }
    }

    // Camera view setup with fallback
    var cameraProviderError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Camera Viewport or Live Visual Canvas
        Box(modifier = Modifier.fillMaxSize()) {
            if (!cameraProviderError) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = if (isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                cameraProviderError = true
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Fallback studio canvas if hardware camera is ungranted or on emulator
            if (cameraProviderError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    SurfaceDark,
                                    CardSurfaceElevated,
                                    BackgroundDark
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(CardSurface)
                                .border(2.dp, NeonMagenta, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎥", fontSize = 42.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isFrontCamera) "Front Camera (Live Studio Preview)" else "Rear Camera (Live Studio Preview)",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Filter: ${selectedFilter.name} ${selectedFilter.emoji}",
                            color = GoldCoin,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 2. Real-time Live Camera Filter Overlay
            if (selectedFilter.tintColorHex != 0x00000000L) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(selectedFilter.tintColorHex))
                )
            }

            // Soft Vignette for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
        }

        // 3. Top Action Controls: Close, Flash, Camera Flip, Selected Music Track
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("close_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                // Music Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable {
                            selectedMusic = if (selectedMusic.contains("Neon Echo")) {
                                "🎵 Leo Carter • Acoustic Sunset"
                            } else {
                                "🎵 Neon Echo • Midnight Pulse"
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🎧", fontSize = 12.sp)
                        Text(
                            text = selectedMusic,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // Flash & Flip
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isFlashOn) GoldCoinDark else Color.Black.copy(alpha = 0.5f))
                            .testTag("camera_flash_btn")
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("flip_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Flip",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Duration & Speed controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Duration Pills (15s, 30s, 60s)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf(15, 30, 60).forEach { sec ->
                        val isSelected = selectedDurationSec == sec
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonMagenta else Color.Transparent)
                                .clickable { selectedDurationSec = sec }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${sec}s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Speed Pills (0.5x, 1x, 2x)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf("0.5x", "1x", "2x").forEach { spd ->
                        val isSelected = selectedSpeed == spd
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricViolet else Color.Transparent)
                                .clickable { selectedSpeed = spd }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = spd,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 4. Bottom Controls: Filters Carousel & Shutter Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Filters Carousel
            Text(
                text = "Live Shading & Glow Filters",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(MockDataProvider.cameraFilters, key = { it.id }) { filter ->
                    val isSelected = selectedFilter.id == filter.id

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.selectCameraFilter(filter) }
                            .testTag("filter_${filter.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) GoldCoin else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(
                                    if (filter.tintColorHex != 0L) Color(filter.tintColorHex)
                                    else CardSurfaceElevated
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = filter.emoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = filter.name,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldCoin else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Shutter / Record Reel Button with Progress Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(86.dp)
                    .clickable {
                        if (!isRecording) {
                            isRecording = true
                        } else {
                            isRecording = false
                            isRecordedComplete = true
                        }
                    }
                    .testTag("camera_record_shutter_btn")
            ) {
                // Circular Progress Ring when recording
                CircularProgressIndicator(
                    progress = { recordingProgress },
                    modifier = Modifier.size(84.dp),
                    color = LiveRed,
                    strokeWidth = 4.dp,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                // Center red record button
                Box(
                    modifier = Modifier
                        .size(if (isRecording) 36.dp else 64.dp)
                        .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                        .background(LiveRed)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isRecording) "Recording... Tap to Stop" else "Tap to Record Reel",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 5. Post Reel Dialog on Recording Completion
        if (isRecordedComplete) {
            AlertDialog(
                onDismissRequest = { isRecordedComplete = false },
                containerColor = SurfaceDark,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎉", fontSize = 22.sp)
                        Text(
                            text = "Publish Your Reel",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Your clip is recorded! Add a caption before publishing to the Reels stream.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = reelCaption,
                            onValueChange = { reelCaption = it },
                            label = { Text("Caption", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMagenta,
                                unfocusedBorderColor = CardSurfaceElevated,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reel_caption_input")
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filter: ${selectedFilter.name} ${selectedFilter.emoji}",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "•", color = TextMuted)
                            Text(
                                text = "Duration: ${selectedDurationSec}s",
                                color = GoldCoin,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.postNewReel(
                                caption = reelCaption,
                                music = selectedMusic,
                                filterName = selectedFilter.name
                            )
                            isRecordedComplete = false
                            onReelPublished()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("publish_reel_confirm_btn")
                    ) {
                        Text("Post Reel Now 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isRecordedComplete = false }) {
                        Text("Retake", color = TextSecondary)
                    }
                }
            )
        }
    }
}
