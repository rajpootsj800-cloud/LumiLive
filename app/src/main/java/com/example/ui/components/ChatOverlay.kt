package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.*

@Composable
fun ChatOverlay(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var textInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Chat Messages Stream (Faded gradient at top, auto-scrolling)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                SingleChatMessageItem(msg = msg)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field & Quick Emojis
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Text Input Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = "Say something in live chat...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    if (textInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendMessage(textInput)
                                textInput = ""
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Quick emoji tap bar
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("🔥", "👏", "💖", "🎉").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable {
                                onSendMessage(emoji)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleChatMessageItem(msg: ChatMessage) {
    if (msg.isSystem) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = msg.message,
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val isGift = msg.giftTransaction != null

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isGift) {
                    Brush.horizontalGradient(
                        listOf(
                            NeonMagenta.copy(alpha = 0.5f),
                            ElectricViolet.copy(alpha = 0.5f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            SurfaceDark.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Level Badge
            if (msg.userLevel > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                msg.userLevel >= 20 -> GoldCoinDark
                                msg.userLevel >= 10 -> ElectricViolet
                                else -> CardSurfaceElevated
                            }
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Lv.${msg.userLevel}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // VIP / Top Gifter Badge
            if (msg.badgeType != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldCoin)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = msg.badgeType,
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Sender name & message
            Text(
                text = "${msg.senderName}: ",
                color = if (isGift) GoldCoin else NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = msg.message,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (isGift) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
