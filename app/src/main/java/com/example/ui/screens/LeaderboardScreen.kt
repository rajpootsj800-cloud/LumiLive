package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.MockDataProvider
import com.example.model.LeaderboardItem
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Top Streamers, 1: Top Gifters

    val items = remember(selectedTab) {
        if (selectedTab == 0) MockDataProvider.topStreamersLeaderboard
        else MockDataProvider.topGiftersLeaderboard
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Title
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "🏆 Hall of Fame",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Daily live rankings & top gifting contributors",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Tab Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Top Streamers", "Top Gifters")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonMagenta else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp)
                                .testTag("leaderboard_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Podium (Top 1, 2, 3)
            item {
                if (items.size >= 3) {
                    PodiumView(
                        first = items[0],
                        second = items[1],
                        third = items[2]
                    )
                }
            }

            // Rank 4+ List
            items(items.drop(3), key = { it.rank }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Rank Number
                            Text(
                                text = "#${item.rank}",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CardSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column {
                                Text(
                                    text = item.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.badge,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Score
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "💎", fontSize = 12.sp)
                            Text(
                                text = "${item.score}",
                                color = GoldCoin,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumView(
    first: LeaderboardItem,
    second: LeaderboardItem,
    third: LeaderboardItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver)
        PodiumColumn(
            item = second,
            rankColor = Color(0xFFC0C0C0),
            rankEmoji = "🥈",
            heightDp = 100
        )

        // 1st Place (Gold)
        PodiumColumn(
            item = first,
            rankColor = GoldCoin,
            rankEmoji = "👑",
            heightDp = 130,
            isFirst = true
        )

        // 3rd Place (Bronze)
        PodiumColumn(
            item = third,
            rankColor = Color(0xFFCD7F32),
            rankEmoji = "🥉",
            heightDp = 80
        )
    }
}

@Composable
private fun PodiumColumn(
    item: LeaderboardItem,
    rankColor: Color,
    rankEmoji: String,
    heightDp: Int,
    isFirst: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Text(text = rankEmoji, fontSize = if (isFirst) 24.sp else 18.sp)
        Box(
            modifier = Modifier
                .size(if (isFirst) 56.dp else 46.dp)
                .clip(CircleShape)
                .background(rankColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.take(1).uppercase(),
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontSize = if (isFirst) 20.sp else 16.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = "💎", fontSize = 10.sp)
            Text(
                text = "${item.score}",
                color = GoldCoin,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(CardSurface)
        )
    }
}
