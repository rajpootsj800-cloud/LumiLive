package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PkOpponent
import com.example.ui.theme.*

@Composable
fun PkBattleBar(
    hostName: String,
    hostScore: Int,
    opponent: PkOpponent?,
    opponentScore: Int,
    modifier: Modifier = Modifier
) {
    if (opponent == null) return

    val total = (hostScore + opponentScore).coerceAtLeast(1)
    val hostRatio = (hostScore.toFloat() / total).coerceIn(0.1f, 0.9f)

    val animatedRatio by animateFloatAsState(
        targetValue = hostRatio,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pkRatio"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Top labels: Host vs Opponent
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Text(
                    text = "$hostName ($hostScore 💎)",
                    color = NeonCyan,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LiveRed)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⚔️ PK 02:45",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${opponent.hostName} ($opponentScore 💎)",
                    color = NeonMagenta,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tug of war progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(CardSurface)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Host portion
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(animatedRatio)
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonCyan, Color(0xFF0099FF))
                            )
                        )
                )

                // Opponent portion
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f - animatedRatio)
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonMagentaDark, NeonMagenta)
                            )
                        )
                )
            }

            // Center Flame VS Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(GoldCoinDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡",
                    fontSize = 11.sp
                )
            }
        }
    }
}
