package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
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
import coil.compose.AsyncImage
import com.example.data.MockDataProvider
import com.example.model.CoinPackage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinPurchaseDialog(
    userCoins: Int,
    userDiamonds: Int,
    onPurchasePack: (CoinPackage) -> Unit,
    onConvertDiamonds: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPackForCheckout by remember { mutableStateOf<CoinPackage?>(null) }
    var isSuccessState by remember { mutableStateOf(false) }
    var convertInput by remember { mutableStateOf("1000") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = TextSecondary.copy(alpha = 0.4f))
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Title and Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🪙", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "LumiLive Coin Store",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Instant recharge for gifting & VIP status",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wallet Balance Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CardSurfaceElevated, SurfaceDark)
                        )
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Your Coin Balance", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 18.sp)
                        Text(
                            text = "$userCoins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldCoin
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = CardSurface
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Earned Diamonds", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "💎", fontSize = 18.sp)
                        Text(
                            text = "$userDiamonds",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Coin Packages Grid
            Text(
                text = "Select Coin Package",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
            ) {
                items(MockDataProvider.sampleCoinPackages, key = { it.id }) { pack ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(14.dp))
                            .clickable {
                                selectedPackForCheckout = pack
                            }
                            .padding(10.dp)
                            .testTag("buy_pack_${pack.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (pack.badgeText != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonMagenta)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = pack.badgeText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(text = "🪙", fontSize = 24.sp)
                            Text(
                                text = String.format("%,d", pack.coins),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldCoin
                            )
                            if (pack.bonusCoins > 0) {
                                Text(
                                    text = "+${String.format("%,d", pack.bonusCoins)} Bonus",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnlineGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonMagentaDark)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = pack.priceUsd,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Diamond to Coin Conversion Drawer item
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Creator Diamond Conversion",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Exchange 10 💎 for 1 🪙 Coin",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { onConvertDiamonds(1000) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("convert_diamonds_button")
                    ) {
                        Text(text = "Exchange 1,000 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Google Play Style 1-Tap Purchase Checkout Sheet
    if (selectedPackForCheckout != null) {
        val pack = selectedPackForCheckout!!

        AlertDialog(
            onDismissRequest = {
                selectedPackForCheckout = null
                isSuccessState = false
            },
            containerColor = SurfaceDark,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Store Checkout",
                        tint = NeonCyan
                    )
                    Text(
                        text = if (isSuccessState) "Payment Successful!" else "Google Play In-App Purchase",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                if (isSuccessState) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = OnlineGreen,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coins Added to Wallet!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "+${String.format("%,d", pack.coins + pack.bonusCoins)} Coins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldCoin
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Product:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "LumiLive ${String.format("%,d", pack.coins)} Coins",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        if (pack.bonusCoins > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Bonus:", color = TextSecondary, fontSize = 13.sp)
                                Text(
                                    text = "+${String.format("%,d", pack.bonusCoins)} Bonus Coins",
                                    color = OnlineGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Price:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = pack.priceUsd,
                                color = GoldCoin,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure",
                                tint = OnlineGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Payment Method: Google Play Balance (Instant)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (isSuccessState) {
                    Button(
                        onClick = {
                            selectedPackForCheckout = null
                            isSuccessState = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OnlineGreen),
                        modifier = Modifier.testTag("done_purchase_button")
                    ) {
                        Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            onPurchasePack(pack)
                            isSuccessState = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        modifier = Modifier.testTag("confirm_purchase_button")
                    ) {
                        Text("1-Tap Buy • ${pack.priceUsd}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!isSuccessState) {
                    TextButton(onClick = { selectedPackForCheckout = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            }
        )
    }
}
