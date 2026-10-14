package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GiftEffectType
import com.example.model.GiftTransaction
import com.example.ui.theme.*

@Composable
fun GiftAnimationOverlay(
    transaction: GiftTransaction?,
    modifier: Modifier = Modifier
) {
    if (transaction == null) return

    val gift = transaction.gift

    // Entry & Exit animation
    val transition = rememberInfiniteTransition(label = "gift_fx")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotateAngle by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    // Horizontal drive across screen for sports car / yacht
    val driveX = remember { Animatable(-300f) }
    val rocketY = remember { Animatable(300f) }

    LaunchedEffect(transaction.id) {
        if (gift.effectType == GiftEffectType.SPORTS_CAR || gift.effectType == GiftEffectType.YACHT) {
            driveX.snapTo(-300f)
            driveX.animateTo(
                targetValue = 350f,
                animationSpec = tween(1800, easing = FastOutSlowInEasing)
            )
        } else if (gift.effectType == GiftEffectType.ROCKET) {
            rocketY.snapTo(400f)
            rocketY.animateTo(
                targetValue = -500f,
                animationSpec = tween(1600, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top Banner for luxury announcement
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            NeonMagenta.copy(alpha = 0.85f),
                            ElectricViolet.copy(alpha = 0.85f),
                            GoldCoinDark.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎁",
                    fontSize = 20.sp
                )
                Column {
                    Text(
                        text = "${transaction.senderName} sent ${transaction.receiverName}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${gift.name} x${transaction.comboCount}!",
                        color = GoldCoin,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                if (transaction.comboCount > 1) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldCoin)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "x${transaction.comboCount}",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Center Animated Asset / Effect based on type
        when (gift.effectType) {
            GiftEffectType.SPORTS_CAR -> {
                Box(
                    modifier = Modifier
                        .offset(x = driveX.value.dp)
                        .scale(pulseScale)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🏎️💨",
                            fontSize = 80.sp
                        )
                        Text(
                            text = "HYPER SPEED CAR!",
                            color = NeonCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            GiftEffectType.ROCKET -> {
                Box(
                    modifier = Modifier
                        .offset(y = rocketY.value.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🚀🔥",
                            fontSize = 84.sp
                        )
                        Text(
                            text = "TO THE MOON!",
                            color = GoldCoin,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            GiftEffectType.DRAGON -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = "🐉🔥",
                        fontSize = 92.sp
                    )
                    Text(
                        text = "IMPERIAL DRAGON SUMMONED!",
                        color = GoldCoin,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
            GiftEffectType.FIREWORKS -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = "🎆✨🎆",
                        fontSize = 80.sp
                    )
                    Text(
                        text = "STADIUM CELEBRATION!",
                        color = NeonMagenta,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
            GiftEffectType.YACHT -> {
                Box(
                    modifier = Modifier
                        .offset(x = (driveX.value * 0.7f).dp)
                        .scale(pulseScale)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🛥️🌊",
                            fontSize = 84.sp
                        )
                        Text(
                            text = "CYBER YACHT ARRIVED!",
                            color = NeonCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
            GiftEffectType.RING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = "💍✨",
                        fontSize = 84.sp
                    )
                    Text(
                        text = "DIAMOND SPARKLE!",
                        color = NeonCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
            else -> {
                // Rose or Hearts
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = "${gift.iconEmoji} ✨",
                        fontSize = 72.sp
                    )
                    Text(
                        text = gift.name,
                        color = NeonMagenta,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
