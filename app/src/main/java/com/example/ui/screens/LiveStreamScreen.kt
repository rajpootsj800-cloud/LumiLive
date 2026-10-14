package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gift
import com.example.model.StreamRoom
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel

@Composable
fun LiveStreamScreen(
    viewModel: LiveStreamViewModel,
    room: StreamRoom,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.leaveRoom()
        onNavigateBack()
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val partySeats by viewModel.partySeats.collectAsState()
    val mySeatIndex by viewModel.mySeatIndex.collectAsState()
    val isMyMicMuted by viewModel.isMyMicMuted.collectAsState()
    val pkHostScore by viewModel.pkHostScore.collectAsState()
    val pkOpponentScore by viewModel.pkOpponentScore.collectAsState()
    val activeGiftAnimation by viewModel.activeGiftAnimation.collectAsState()
    val flyingHearts by viewModel.flyingHearts.collectAsState()
    val comboCounter by viewModel.comboCounter.collectAsState()
    val soundEffectTrigger by viewModel.soundEffectTrigger.collectAsState()

    var showGiftDrawer by remember { mutableStateOf(false) }
    var showCoinShop by remember { mutableStateOf(false) }
    var showSoundboard by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.addFlyingHeart()
                        viewModel.addFlyingHeart()
                    },
                    onTap = {
                        viewModel.addFlyingHeart()
                    }
                )
            }
    ) {
        // 1. Video Stage Background (Solo live, PK Battle, or Multi-Seat Party)
        VideoStreamStage(
            room = room,
            partySeats = partySeats,
            mySeatIndex = mySeatIndex,
            isMyMicMuted = isMyMicMuted,
            onSeatClick = { seatIdx -> viewModel.takeSeat(seatIdx) },
            onToggleMic = { viewModel.toggleMyMic() },
            onOpenSoundboard = { showSoundboard = true },
            pkHostScore = pkHostScore,
            pkOpponentScore = pkOpponentScore,
            soundEffectTrigger = soundEffectTrigger
        )

        // 2. Floating Animated Hearts
        FloatingHeartsView(hearts = flyingHearts)

        // 3. Top Stream Header Bar
        StreamHeaderBar(
            room = room,
            onFollow = { viewModel.followHost(it) },
            onClose = {
                viewModel.leaveRoom()
                onNavigateBack()
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 4. Live Chat Overlay & Bottom Stream Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
        ) {
            // Live Chat Messages
            ChatOverlay(
                messages = chatMessages,
                onSendMessage = { text -> viewModel.sendMessage(text) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wallet Coin Indicator Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable { showCoinShop = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("stream_coin_wallet_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Text(
                            text = "${currentUser.coinsBalance}",
                            color = GoldCoin,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+",
                            color = NeonMagenta,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Right action icons: Soundboard, Like Heart, and Gift Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Party Soundboard
                    IconButton(
                        onClick = { showSoundboard = true },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("stream_soundboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Sound FX",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Floating Heart Button
                    IconButton(
                        onClick = { viewModel.addFlyingHeart() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("stream_like_heart_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = LiveRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // GIFT BUTTON (Highlight glowing button)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(NeonMagenta)
                            .clickable { showGiftDrawer = true }
                            .testTag("stream_open_gift_drawer_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Send Gifts",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 5. Full Screen Animated Gift Overlay
        GiftAnimationOverlay(transaction = activeGiftAnimation)

        // 6. Gift Drawer Bottom Sheet
        if (showGiftDrawer) {
            GiftDrawerSheet(
                userCoins = currentUser.coinsBalance,
                comboCount = comboCounter,
                onSendGift = { gift, qty ->
                    viewModel.sendGift(gift, qty)
                },
                onOpenCoinShop = {
                    showGiftDrawer = false
                    showCoinShop = true
                },
                onDismiss = { showGiftDrawer = false }
            )
        }

        // 7. Coin Purchase Store Dialog
        if (showCoinShop) {
            CoinPurchaseDialog(
                userCoins = currentUser.coinsBalance,
                userDiamonds = currentUser.diamondsEarned,
                onPurchasePack = { pack ->
                    viewModel.buyCoinPackage(pack)
                },
                onConvertDiamonds = { diamonds ->
                    viewModel.convertDiamondsToCoins(diamonds)
                },
                onDismiss = { showCoinShop = false }
            )
        }

        // 8. Party Soundboard Dialog
        if (showSoundboard) {
            PartySoundboardDialog(
                onPlaySound = { sound ->
                    viewModel.triggerPartySound(sound)
                    showSoundboard = false
                },
                onDismiss = { showSoundboard = false }
            )
        }
    }
}
