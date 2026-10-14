package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoomType
import com.example.model.StreamRoom
import com.example.ui.components.CoinPurchaseDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel

@Composable
fun ExploreHomeScreen(
    viewModel: LiveStreamViewModel,
    onRoomSelected: (StreamRoom) -> Unit,
    onGoLiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms by viewModel.rooms.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showCoinShop by remember { mutableStateOf(false) }

    val categories = listOf("All", "Party Rooms", "PK Battles", "Music & DJ")

    val filteredRooms = remember(rooms, selectedCategory) {
        if (selectedCategory == "All") {
            rooms
        } else {
            rooms.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // App Bar: Logo & Coin Wallet Chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonMagenta, ElectricViolet))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 18.sp)
                    }
                    Text(
                        text = "LumiLive",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Coin Balance Chip with "+" to top up
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardSurfaceElevated)
                        .clickable { showCoinShop = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("explore_coin_chip"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Text(
                        text = "${currentUser.coinsBalance}",
                        color = GoldCoin,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NeonMagenta),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Coins",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Stream List & Hero Banner
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Hero Banner (Spans 2 columns)
                item(span = { GridItemSpan(2) }) {
                    HeroFestiveBanner(
                        coins = currentUser.coinsBalance,
                        onRechargeClick = { showCoinShop = true }
                    )
                }

                // Category Filter Pills (Spans 2 columns)
                item(span = { GridItemSpan(2) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) NeonMagenta else CardSurface
                                    )
                                    .clickable { viewModel.selectCategory(cat) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("category_pill_$cat")
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Active Stream Cards
                items(filteredRooms, key = { it.id }) { room ->
                    StreamRoomCard(
                        room = room,
                        onClick = { onRoomSelected(room) }
                    )
                }
            }
        }

        // Floating "Go Live" Action Button
        ExtendedFloatingActionButton(
            onClick = onGoLiveClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 84.dp)
                .testTag("go_live_fab"),
            containerColor = NeonMagenta,
            contentColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Go Live",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Go Live",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Coin Purchase Modal
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
    }
}

@Composable
private fun HeroFestiveBanner(
    coins: Int,
    onRechargeClick: () -> Unit
) {
    val context = LocalContext.current
    val bannerId = remember {
        context.resources.getIdentifier("party_coins_banner_1791962627817", "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bannerId != 0) {
                Image(
                    painter = painterResource(id = bannerId),
                    contentDescription = "Celebration Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldCoinDark)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎉 LIVE PARTY CARNIVAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Double Diamonds & Super Gifting!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Top up coins & unlock exclusive Dragon & Car FX",
                    fontSize = 11.sp,
                    color = GoldCoin
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onRechargeClick,
                    modifier = Modifier.height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(text = "Top Up Store", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StreamRoomCard(
    room: StreamRoom,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resourceId = remember(room.coverDrawableName) {
        val id = context.resources.getIdentifier(room.coverDrawableName, "drawable", context.packageName)
        if (id != 0) id else context.resources.getIdentifier("stream_dj_neon_1791962557863", "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("room_card_${room.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail with Live badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = room.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(CardSurfaceElevated))
                }

                // Dark gradient at bottom of image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Live Badge & Viewers
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LiveRed)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${room.viewerCount}",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Room Type Pill (PK Battle / Party 8 Seats)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (room.roomType) {
                                RoomType.PK_BATTLE -> NeonMagenta
                                RoomType.MULTI_GUEST_PARTY -> ElectricViolet
                                RoomType.SOLO_LIVE -> CardSurfaceElevated
                            }
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (room.roomType) {
                            RoomType.PK_BATTLE -> "⚔️ PK"
                            RoomType.MULTI_GUEST_PARTY -> "🎉 8 Seats"
                            RoomType.SOLO_LIVE -> "Solo"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Host Name on image
                Text(
                    text = room.hostName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }

            // Card Bottom info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = room.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.category,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "💎", fontSize = 10.sp)
                        Text(
                            text = "${room.totalDiamonds}",
                            color = GoldCoin,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
