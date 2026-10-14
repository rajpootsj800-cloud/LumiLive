package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PartySeat
import com.example.model.RoomType
import com.example.model.StreamRoom
import com.example.ui.theme.*

@Composable
fun VideoStreamStage(
    room: StreamRoom,
    partySeats: List<PartySeat>,
    mySeatIndex: Int?,
    isMyMicMuted: Boolean,
    onSeatClick: (Int) -> Unit,
    onToggleMic: () -> Unit,
    onOpenSoundboard: () -> Unit,
    pkHostScore: Int,
    pkOpponentScore: Int,
    soundEffectTrigger: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resourceId = remember(room.coverDrawableName) {
        val id = context.resources.getIdentifier(room.coverDrawableName, "drawable", context.packageName)
        if (id != 0) id else context.resources.getIdentifier("stream_dj_neon_1791962557863", "drawable", context.packageName)
    }

    // Dynamic camera breath animation
    val infiniteTransition = rememberInfiniteTransition(label = "streamAnim")
    val subtleZoom by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )

    Box(modifier = modifier.fillMaxSize()) {
        when (room.roomType) {
            RoomType.PK_BATTLE -> {
                // Split Screen PK Battle View
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left: Host
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (resourceId != 0) {
                            Image(
                                painter = painterResource(id = resourceId),
                                contentDescription = room.hostName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(subtleZoom)
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(SurfaceDark))
                        }

                        // Host Name Pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔵 ${room.hostName}",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(GoldCoinDark)
                    )

                    // Right: Opponent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        val opponentResId = context.resources.getIdentifier("stream_dj_neon_1791962557863", "drawable", context.packageName)
                        if (opponentResId != 0) {
                            Image(
                                painter = painterResource(id = opponentResId),
                                contentDescription = room.pkOpponent?.hostName ?: "Opponent",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(subtleZoom)
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(CardSurface))
                        }

                        // Opponent Name Pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${room.pkOpponent?.hostName ?: "Opponent"} 🔴",
                                color = NeonMagenta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Reusable PK Battle Tug-of-war Bar at top
                PkBattleProgressBar(
                    hostScore = pkHostScore.toLong(),
                    opponentScore = pkOpponentScore.toLong(),
                    hostName = room.hostName,
                    opponentName = room.pkOpponent?.hostName ?: "Opponent",
                    battleTimeRemainingSeconds = 165,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 74.dp)
                        .padding(horizontal = 8.dp)
                )
            }

            RoomType.MULTI_GUEST_PARTY -> {
                // Party Room Stage
                Box(modifier = Modifier.fillMaxSize()) {
                    if (resourceId != 0) {
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = room.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.45f)
                        )
                    }

                    // Deep atmospheric gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        BackgroundDark.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Party Room Center Visual: Host DJ Avatar & 8-Seats Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 76.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Host Avatar Stage
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .border(2.dp, GoldCoin, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(ElectricViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👑",
                                    fontSize = 28.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldCoinDark)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Host",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = room.hostName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = room.title,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 8-Seat Guest Grid
                        PartySeatsGrid(
                            seats = partySeats,
                            mySeatIndex = mySeatIndex,
                            isMyMicMuted = isMyMicMuted,
                            onSeatClick = onSeatClick,
                            onToggleMic = onToggleMic,
                            onOpenSoundboard = onOpenSoundboard
                        )
                    }
                }
            }

            RoomType.SOLO_LIVE -> {
                // Solo Full-Bleed Live Broadcast Stage
                Box(modifier = Modifier.fillMaxSize()) {
                    if (resourceId != 0) {
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = room.hostName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(subtleZoom)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SurfaceDark)
                        )
                    }

                    // Soft vignette at top and bottom for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.65f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                }
            }
        }

        // Sound Effect Broadcast Flash Alert
        if (soundEffectTrigger != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(1.dp, GoldCoin, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🔊", fontSize = 24.sp)
                    Text(
                        text = "$soundEffectTrigger!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldCoin
                    )
                }
            }
        }
    }
}
