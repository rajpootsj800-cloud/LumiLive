package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.FlyingHeart

@Composable
fun FloatingHeartsView(
    hearts: List<FlyingHeart>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        hearts.forEach { heart ->
            key(heart.id) {
                SingleRisingHeart(heart = heart)
            }
        }
    }
}

@Composable
private fun SingleRisingHeart(heart: FlyingHeart) {
    val heartEmojis = listOf("💖", "❤️", "💜", "💙", "✨", "🔥")
    val emoji = heartEmojis[heart.colorIndex % heartEmojis.size]

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(heart.id) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val progress = animProgress.value
    val yOffset = -progress * 380f // rises up
    val xOffset = kotlin.math.sin(progress * 8f) * 35f - (heart.xOffsetRatio * 90f)
    val alpha = (1f - progress).coerceIn(0f, 1f)

    if (progress < 1f) {
        Text(
            text = emoji,
            fontSize = heart.size.sp,
            modifier = Modifier
                .padding(end = 40.dp, bottom = 90.dp)
                .offset(x = xOffset.dp, y = yOffset.dp)
                .alpha(alpha)
        )
    }
}
