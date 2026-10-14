package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.Gift
import com.example.model.GiftCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDrawerSheet(
    userCoins: Int,
    comboCount: Int,
    onSendGift: (Gift, Int) -> Unit,
    onOpenCoinShop: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<GiftCategory?>(null) }
    var selectedGift by remember { mutableStateOf(MockDataProvider.sampleGifts[0]) }
    var selectedQuantity by remember { mutableStateOf(1) }

    val filteredGifts = remember(selectedCategory) {
        if (selectedCategory == null) {
            MockDataProvider.sampleGifts
        } else {
            MockDataProvider.sampleGifts.filter { it.category == selectedCategory }
        }
    }

    // Pulse animation for combo button
    val infiniteTransition = rememberInfiniteTransition(label = "combo")
    val comboScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "comboPulse"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = TextSecondary.copy(alpha = 0.4f))
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header: Category Tabs & Coin Wallet Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tabs = listOf(
                        "All" to null,
                        "Popular" to GiftCategory.POPULAR,
                        "Luxury" to GiftCategory.LUXURY,
                        "Special FX" to GiftCategory.SPECIAL_FX
                    )

                    tabs.forEach { (label, cat) ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) NeonMagenta else CardSurface)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Coin Wallet Chip with "+" Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurfaceElevated)
                        .clickable { onOpenCoinShop() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 14.sp)
                    Text(
                        text = "$userCoins",
                        color = GoldCoin,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(NeonMagenta),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Coins",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gifts Grid (8-9 items)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredGifts, key = { it.id }) { gift ->
                    val isSelected = selectedGift.id == gift.id
                    val canAfford = userCoins >= gift.coinCost

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CardSurfaceElevated else CardSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GoldCoin else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedGift = gift
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = gift.iconEmoji,
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canAfford) Color.White else TextMuted,
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(text = "🪙", fontSize = 10.sp)
                                Text(
                                    text = String.format("%,d", gift.coinCost),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (canAfford) GoldCoin else LiveRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Actions: Quantity Selector & Combo Send Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(1, 5, 10, 99).forEach { count ->
                        val isCountSelected = selectedQuantity == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCountSelected) ElectricViolet else CardSurface)
                                .clickable { selectedQuantity = count }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "x$count",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCountSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Send Button with Combo Multiplier state
                val isComboMode = comboCount > 0
                val buttonModifier = if (isComboMode) {
                    Modifier
                        .scale(comboScale)
                        .testTag("send_gift_button")
                } else {
                    Modifier.testTag("send_gift_button")
                }

                Button(
                    onClick = {
                        onSendGift(selectedGift, selectedQuantity)
                    },
                    modifier = buttonModifier
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isComboMode) GoldCoinDark else NeonMagenta
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    if (isComboMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🔥 COMBO", fontWeight = FontWeight.Black, color = Color.White)
                            Text(
                                text = "x$comboCount",
                                fontWeight = FontWeight.Black,
                                color = GoldCoin,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "Send", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = "(${String.format("%,d", selectedGift.coinCost * selectedQuantity)}🪙)",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
