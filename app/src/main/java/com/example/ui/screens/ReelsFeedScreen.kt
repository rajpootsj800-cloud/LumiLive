package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReelItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LiveStreamViewModel

@Composable
fun ReelsFeedScreen(
    viewModel: LiveStreamViewModel,
    onOpenCameraStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reels by viewModel.reels.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val reelComments by viewModel.reelComments.collectAsState()
    val flyingHearts by viewModel.flyingHearts.collectAsState()
    val comboCounter by viewModel.comboCounter.collectAsState()
    val activeGiftAnimation by viewModel.activeGiftAnimation.collectAsState()

    val pagerState = rememberPagerState(pageCount = { reels.size })

    var showCommentsSheet by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showCoinShop by remember { mutableStateOf(false) }
    var activeReelForAction by remember { mutableStateOf<ReelItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (reels.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = reels[page]
                SingleReelPage(
                    reel = reel,
                    onDoubleTap = {
                        viewModel.addFlyingHeart()
                        if (!reel.isLiked) {
                            viewModel.toggleLikeReel(reel.id)
                        }
                    },
                    onLikeClick = {
                        viewModel.toggleLikeReel(reel.id)
                    },
                    onCommentsClick = {
                        activeReelForAction = reel
                        showCommentsSheet = true
                    },
                    onGiftClick = {
                        activeReelForAction = reel
                        showGiftSheet = true
                    },
                    onShareClick = {
                        viewModel.sendMessage("Shared ${reel.creatorName}'s Reel! 🔗")
                    },
                    onCameraClick = onOpenCameraStudio,
                    onFollowClick = {
                        viewModel.followHost(reel.creatorHandle)
                    }
                )
            }
        }

        // Top Navigation Header: "Reels" & Camera Shortcut
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reels",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            // Camera Studio Icon
            IconButton(
                onClick = onOpenCameraStudio,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("reels_open_camera_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Record Reel",
                    tint = NeonMagenta,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Floating Hearts Overlay
        FloatingHeartsView(hearts = flyingHearts)

        // Gift Animation Overlay
        GiftAnimationOverlay(transaction = activeGiftAnimation)

        // Comments Bottom Sheet
        if (showCommentsSheet && activeReelForAction != null) {
            ReelCommentsSheet(
                comments = reelComments,
                onAddComment = { text ->
                    viewModel.addReelComment(text)
                },
                onDismiss = { showCommentsSheet = false }
            )
        }

        // Gift Drawer Sheet
        if (showGiftSheet && activeReelForAction != null) {
            GiftDrawerSheet(
                userCoins = currentUser.coinsBalance,
                comboCount = comboCounter,
                onSendGift = { gift, count ->
                    viewModel.giftReel(activeReelForAction!!.id, gift, count)
                },
                onOpenCoinShop = {
                    showGiftSheet = false
                    showCoinShop = true
                },
                onDismiss = { showGiftSheet = false }
            )
        }

        // Coin Shop Sheet
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
private fun SingleReelPage(
    reel: ReelItem,
    onDoubleTap: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onGiftClick: () -> Unit,
    onShareClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    val context = LocalContext.current
    val resourceId = remember(reel.coverDrawableName) {
        val id = context.resources.getIdentifier(reel.coverDrawableName, "drawable", context.packageName)
        if (id != 0) id else context.resources.getIdentifier("stream_dj_neon_1791962557863", "drawable", context.packageName)
    }

    // Rotating Disc Animation
    val infiniteTransition = rememberInfiniteTransition(label = "discRotate")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Bouncy scale for like button
    val likeScale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() }
                )
            }
    ) {
        // Full bleed background video representation
        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = reel.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
            )
        }

        // Vignette gradients for text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Right-hand interaction bar: Profile, Like, Comment, Gift, Share, Camera
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with "+" Follow badge
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta)
                        .clickable { onFollowClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.creatorName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(y = 6.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                        .clickable { onFollowClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Like Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like Reel",
                    tint = if (reel.isLiked) LiveRed else Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .scale(likeScale.value)
                )
                Text(
                    text = formatCount(reel.likesCount),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onCommentsClick() }
                    .testTag("reel_comments_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = "Comments",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "${reel.commentsCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Gift Action (Glowing gold/magenta)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onGiftClick() }
                    .testTag("reel_gift_btn")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Send Gift",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Gift",
                    color = GoldCoin,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onShareClick() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

            // Rotating Vinyl Disc (Music icon)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .rotate(discRotation)
                    .clip(CircleShape)
                    .background(CardSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💿", fontSize = 20.sp)
            }
        }

        // Bottom Details: Creator Name, Caption, Tags, Audio Track
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 24.dp)
        ) {
            // Creator Name & Filter badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = reel.creatorName,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                if (reel.filterName != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✨ ${reel.filterName}",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption
            Text(
                text = reel.caption,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Tags
            if (reel.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    reel.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Music track marquee
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = reel.musicTrack,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
