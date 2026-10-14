package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Star
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
import com.example.data.MockDataProvider
import com.example.model.CoinPackage
import com.example.ui.components.CoinPurchaseDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel

@Composable
fun CoinShopScreen(
    viewModel: LiveStreamViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showPurchaseDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val bannerId = remember {
        context.resources.getIdentifier("party_coins_banner_1791962627817", "drawable", context.packageName)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🪙 Coin Store & VIP",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Purchase coins to send gifts and unlock VIP badges",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Wallet Balances Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Available Balance", fontSize = 12.sp, color = TextSecondary)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "🪙", fontSize = 24.sp)
                                    Text(
                                        text = "${currentUser.coinsBalance} Coins",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldCoin
                                    )
                                }
                            }

                            Button(
                                onClick = { showPurchaseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("shop_recharge_button")
                            ) {
                                Text(text = "+ Recharge", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CardSurfaceElevated)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "💎", fontSize = 18.sp)
                                Column {
                                    Text(text = "Diamonds Received", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${currentUser.diamondsEarned} 💎",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.convertDiamondsToCoins(1000)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("convert_diamonds_shop_button")
                            ) {
                                Text(text = "Convert to Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Coin Packages Grid
            item {
                Text(
                    text = "Top-Up Packages",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MockDataProvider.sampleCoinPackages.chunked(2).forEach { rowPacks ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPacks.forEach { pack ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showPurchaseDialog = true }
                                        .testTag("shop_pack_${pack.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (pack.badgeText != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(NeonMagenta)
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = pack.badgeText,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }

                                        Text(text = "🪙", fontSize = 28.sp)
                                        Text(
                                            text = "${String.format("%,d", pack.coins)} Coins",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = GoldCoin
                                        )
                                        if (pack.bonusCoins > 0) {
                                            Text(
                                                text = "+${String.format("%,d", pack.bonusCoins)} Bonus",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = OnlineGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { showPurchaseDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = pack.priceUsd,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowPacks.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // VIP Privileges Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "👑", fontSize = 20.sp)
                            Text(
                                text = "VIP Status: ${currentUser.vipTier}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GoldCoin
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Level up your VIP tier by sending gifts & coin purchases.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val perks = listOf(
                            "Exclusive chat glowing name & VIP badge",
                            "Full-screen luxury entrance banner effect",
                            "Priority seat access in multi-guest party rooms",
                            "Unlock Imperial Dragon & Hyper Supercar gifts"
                        )

                        perks.forEach { perk ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = OnlineGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = perk, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (showPurchaseDialog) {
            CoinPurchaseDialog(
                userCoins = currentUser.coinsBalance,
                userDiamonds = currentUser.diamondsEarned,
                onPurchasePack = { pack ->
                    viewModel.buyCoinPackage(pack)
                },
                onConvertDiamonds = { diamonds ->
                    viewModel.convertDiamondsToCoins(diamonds)
                },
                onDismiss = { showPurchaseDialog = false }
            )
        }
    }
}
