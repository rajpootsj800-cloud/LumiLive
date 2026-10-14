package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PartySeat
import com.example.ui.theme.*

@Composable
fun PartySeatsGrid(
    seats: List<PartySeat>,
    mySeatIndex: Int?,
    isMyMicMuted: Boolean,
    onSeatClick: (Int) -> Unit,
    onToggleMic: () -> Unit,
    onOpenSoundboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.55f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Party info and Mic / Soundboard controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🎉", fontSize = 16.sp)
                    Text(
                        text = "Guest Voice & Video Seats (8)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Soundboard button
                    IconButton(
                        onClick = onOpenSoundboard,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardSurfaceElevated)
                            .testTag("soundboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Soundboard",
                            tint = GoldCoin,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // My Mic Toggle (if seated)
                    if (mySeatIndex != null) {
                        IconButton(
                            onClick = onToggleMic,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isMyMicMuted) LiveRed else OnlineGreen)
                                .testTag("toggle_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isMyMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Toggle Mic",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2 rows of 4 seats
            val firstRow = seats.take(4)
            val secondRow = seats.drop(4).take(4)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                firstRow.forEach { seat ->
                    SingleSeatItem(
                        seat = seat,
                        isMe = mySeatIndex == seat.seatIndex,
                        onClick = { onSeatClick(seat.seatIndex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                secondRow.forEach { seat ->
                    SingleSeatItem(
                        seat = seat,
                        isMe = mySeatIndex == seat.seatIndex,
                        onClick = { onSeatClick(seat.seatIndex) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleSeatItem(
    seat: PartySeat,
    isMe: Boolean,
    onClick: () -> Unit
) {
    val user = seat.user

    // Speaking pulse
    val infiniteTransition = rememberInfiniteTransition(label = "speaking")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .testTag("seat_${seat.seatIndex}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(46.dp)
        ) {
            if (user != null) {
                // Occupied seat
                val borderModifier = if (seat.isSpeaking && !seat.isMuted) {
                    Modifier
                        .scale(haloPulse)
                        .border(2.dp, NeonCyan, CircleShape)
                } else if (isMe) {
                    Modifier.border(2.dp, NeonMagenta, CircleShape)
                } else {
                    Modifier.border(1.dp, CardSurfaceElevated, CircleShape)
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .then(borderModifier)
                        .clip(CircleShape)
                        .background(
                            when (seat.seatIndex % 4) {
                                0 -> ElectricViolet
                                1 -> NeonMagentaDark
                                2 -> CardSurfaceElevated
                                else -> GoldCoinDark
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                // Mic status badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (seat.isMuted) LiveRed else OnlineGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (seat.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic Status",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            } else {
                // Empty seat slot
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, TextMuted.copy(alpha = 0.5f), CircleShape)
                        .clip(CircleShape)
                        .background(CardSurface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Take Seat",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = if (user != null) {
                if (isMe) "You" else user.name.take(6)
            } else {
                "Seat ${seat.seatIndex + 1}"
            },
            fontSize = 10.sp,
            color = if (isMe) NeonMagenta else if (user != null) Color.White else TextMuted,
            fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
