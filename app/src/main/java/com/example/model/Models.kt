package com.example.model

enum class RoomType {
    SOLO_LIVE,
    MULTI_GUEST_PARTY,
    PK_BATTLE
}

data class PkOpponent(
    val hostName: String,
    val hostHandle: String,
    val hostAvatar: String,
    val initialScore: Int = 1200
)

data class StreamRoom(
    val id: String,
    val title: String,
    val hostName: String,
    val hostHandle: String,
    val hostAvatar: String,
    val hostBio: String,
    val coverDrawableName: String,
    val viewerCount: Int,
    val totalDiamonds: Long,
    val roomType: RoomType,
    val tags: List<String>,
    val category: String,
    val isLive: Boolean = true,
    val pkOpponent: PkOpponent? = null
)

data class PartySeat(
    val seatIndex: Int,
    val user: UserProfile? = null,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false
)

enum class GiftCategory {
    POPULAR,
    LUXURY,
    SPECIAL_FX
}

enum class GiftEffectType {
    ROSE,
    HEARTS,
    RING,
    SPORTS_CAR,
    DRAGON,
    YACHT,
    ROCKET,
    FIREWORKS
}

data class Gift(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val coinCost: Int,
    val category: GiftCategory,
    val effectType: GiftEffectType,
    val description: String,
    val isComboAllowed: Boolean = true
)

data class GiftTransaction(
    val id: String,
    val senderName: String,
    val senderAvatar: String,
    val receiverName: String,
    val gift: Gift,
    val comboCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val message: String,
    val userLevel: Int = 1,
    val badgeType: String? = null, // "VIP", "Top Gifter", "Host", "Admin"
    val isSystem: Boolean = false,
    val giftTransaction: GiftTransaction? = null
)

data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val priceUsd: String,
    val badgeText: String? = null
)

data class UserProfile(
    val id: String,
    val name: String,
    val handle: String,
    val avatar: String,
    val level: Int = 12,
    val vipTier: String = "VIP 2",
    val coinsBalance: Int = 850,
    val diamondsEarned: Int = 34200,
    val followingCount: Int = 48,
    val fansCount: Int = 1250,
    val isFollowing: Boolean = false
)

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val handle: String,
    val score: Long,
    val avatar: String,
    val level: Int,
    val badge: String
)

data class ReelItem(
    val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String,
    val coverDrawableName: String,
    val caption: String,
    val musicTrack: String,
    val likesCount: Int,
    val commentsCount: Int,
    val giftsDiamondCount: Long,
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false,
    val tags: List<String> = emptyList(),
    val filterName: String? = null
)

data class ReelComment(
    val id: String,
    val authorName: String,
    val text: String,
    val timeAgo: String,
    val likes: Int = 0
)

data class CameraFilter(
    val id: String,
    val name: String,
    val emoji: String,
    val tintColorHex: Long = 0x00000000,
    val description: String = ""
)

