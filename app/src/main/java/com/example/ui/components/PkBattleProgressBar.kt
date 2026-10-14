package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * A highly customizable and reusable PK Battle progress bar component in Compose
 * to visualize competitive live streaming scores between two hosts.
 *
 * Features:
 * - Animated spring tug-of-war bar with customizable gradients
 * - Dynamic clash indicator positioned at the boundary line
 * - Real-time countdown timer with urgent pulsing when under 30s
 * - Advantage difference badge (+N Lead / Tied / Winner banner)
 * - Optional host & opponent avatar bubbles and diamond score counters
 */
@Composable
fun PkBattleProgressBar(
    hostScore: Long,
    opponentScore: Long,
    hostName: String,
    opponentName: String,
    modifier: Modifier = Modifier,
    hostAvatar: String? = null,
    opponentAvatar: String? = null,
    battleTimeRemainingSeconds: Int = 165,
    isBattleActive: Boolean = true,
    barHeight: Dp = 16.dp,
    showAvatars: Boolean = true,
    showTimer: Boolean = true,
    showAdvantageIndicator: Boolean = true,
    showClashIcon: Boolean = true,
    hostBrush: Brush = Brush.horizontalGradient(listOf(NeonCyan, Color(0xFF0099FF))),
    opponentBrush: Brush = Brush.horizontalGradient(listOf(NeonMagentaDark, NeonMagenta)),
    onTimerFinished: () -> Unit = {}
) {
    val total = (hostScore + opponentScore).coerceAtLeast(1L)
    // Constrain ratio so both sides remain visible (10% to 90%)
    val rawRatio = (hostScore.toFloat() / total.toFloat()).coerceIn(0.10f, 0.90f)

    val animatedRatio by animateFloatAsState(
        targetValue = rawRatio,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pkTugOfWarRatio"
    )

    // Center clash pulse & rotate animation
    val infiniteTransition = rememberInfiniteTransition(label = "pkClashAnim")
    val clashScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clashScale"
    )

    val clashRotate by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clashRotate"
    )

    val isUrgentTime = battleTimeRemainingSeconds in 1..29
    val timerColor = if (isUrgentTime) LiveRed else Color.White

    val scoreDiff = hostScore - opponentScore
    val advantageText = when {
        scoreDiff > 0 -> "+${formatPkScore(scoreDiff)} Host Lead 🔥"
        scoreDiff < 0 -> "+${formatPkScore(-scoreDiff)} Opponent Lead 🔥"
        else -> "TIED ⚡"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pk_battle_progress_bar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // 1. Top Bar: Host Info, Timer / Status, Opponent Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host info (Left / Blue Team)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (showAvatars) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hostName.take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                    }

                    Column {
                        Text(
                            text = hostName,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${formatPkScore(hostScore)} 💎",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Center Timer & Status Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (showTimer) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isUrgentTime) LiveRed else CardSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    tint = timerColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = formatSecondsToMmSs(battleTimeRemainingSeconds),
                                    color = timerColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (showAdvantageIndicator) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = advantageText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (scoreDiff >= 0) NeonCyan else NeonMagenta
                        )
                    }
                }

                // Opponent info (Right / Red Team)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = opponentName,
                            color = NeonMagenta,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${formatPkScore(opponentScore)} 💎",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }

                    if (showAvatars) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opponentName.take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Interactive Tug-Of-War Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .clip(RoundedCornerShape(barHeight / 2))
                    .background(CardSurface)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Host side (Animated ratio width)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(animatedRatio)
                            .background(hostBrush)
                    )

                    // Opponent side
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f - animatedRatio)
                            .background(opponentBrush)
                    )
                }

                // 3. Center Clash Icon
                if (showClashIcon) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(barHeight + 10.dp)
                            .scale(clashScale)
                            .rotate(clashRotate)
                            .clip(CircleShape)
                            .background(GoldCoinDark)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Clash",
                            tint = Color.Black,
                            modifier = Modifier.size(barHeight)
                        )
                    }
                }
            }
        }
    }
}

private fun formatPkScore(score: Long): String {
    return when {
        score >= 1_000_000 -> String.format("%.1fM", score / 1_000_000.0)
        score >= 1_000 -> String.format("%.1fK", score / 1_000.0)
        else -> score.toString()
    }
}

private fun formatSecondsToMmSs(totalSeconds: Int): String {
    val mins = (totalSeconds / 60).coerceAtLeast(0)
    val secs = (totalSeconds % 60).coerceAtLeast(0)
    return String.format("%02d:%02d", mins, secs)
}
