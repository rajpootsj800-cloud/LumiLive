package com.example.data

import com.example.model.*

object MockDataProvider {

    val defaultUser = UserProfile(
        id = "user_me",
        name = "StarGazer",
        handle = "@stargazer",
        avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
        level = 18,
        vipTier = "VIP 3",
        coinsBalance = 25000,
        diamondsEarned = 86000,
        followingCount = 64,
        fansCount = 1820
    )

    val sampleGifts = listOf(
        Gift(
            id = "gift_rose",
            name = "Neon Rose",
            iconEmoji = "🌹",
            coinCost = 100,
            category = GiftCategory.POPULAR,
            effectType = GiftEffectType.ROSE,
            description = "Sweet romantic gesture"
        ),
        Gift(
            id = "gift_heart",
            name = "Heart Beam",
            iconEmoji = "💖",
            coinCost = 250,
            category = GiftCategory.POPULAR,
            effectType = GiftEffectType.HEARTS,
            description = "Burst of glowing hearts"
        ),
        Gift(
            id = "gift_mic",
            name = "Golden Mic",
            iconEmoji = "🎤",
            coinCost = 500,
            category = GiftCategory.POPULAR,
            effectType = GiftEffectType.HEARTS,
            description = "Show love to the vocals"
        ),
        Gift(
            id = "gift_ring",
            name = "Diamond Ring",
            iconEmoji = "💍",
            coinCost = 1000,
            category = GiftCategory.POPULAR,
            effectType = GiftEffectType.RING,
            description = "Sparkling luxury promise"
        ),
        Gift(
            id = "gift_car",
            name = "Hyper Supercar",
            iconEmoji = "🏎️",
            coinCost = 5000,
            category = GiftCategory.LUXURY,
            effectType = GiftEffectType.SPORTS_CAR,
            description = "Roars across the stream!"
        ),
        Gift(
            id = "gift_yacht",
            name = "Cyber Yacht",
            iconEmoji = "🛥️",
            coinCost = 15000,
            category = GiftCategory.LUXURY,
            effectType = GiftEffectType.YACHT,
            description = "Luxury mega cruiser"
        ),
        Gift(
            id = "gift_rocket",
            name = "Cosmic Rocket",
            iconEmoji = "🚀",
            coinCost = 30000,
            category = GiftCategory.LUXURY,
            effectType = GiftEffectType.ROCKET,
            description = "Blasts host to the stars"
        ),
        Gift(
            id = "gift_fireworks",
            name = "Gala Fireworks",
            iconEmoji = "🎆",
            coinCost = 10000,
            category = GiftCategory.SPECIAL_FX,
            effectType = GiftEffectType.FIREWORKS,
            description = "Massive stadium celebration"
        ),
        Gift(
            id = "gift_dragon",
            name = "Imperial Dragon",
            iconEmoji = "🐉",
            coinCost = 50000,
            category = GiftCategory.SPECIAL_FX,
            effectType = GiftEffectType.DRAGON,
            description = "Mythical creature ignites the chat"
        )
    )

    val sampleCoinPackages = listOf(
        CoinPackage(
            id = "coins_10000",
            coins = 10000,
            bonusCoins = 0,
            priceUsd = "$2.00",
            badgeText = "BEST STARTER"
        ),
        CoinPackage(
            id = "coins_30000",
            coins = 30000,
            bonusCoins = 5000,
            priceUsd = "$5.99",
            badgeText = "POPULAR"
        ),
        CoinPackage(
            id = "coins_65000",
            coins = 65000,
            bonusCoins = 15000,
            priceUsd = "$11.99",
            badgeText = "BEST VALUE"
        ),
        CoinPackage(
            id = "coins_150000",
            coins = 150000,
            bonusCoins = 40000,
            priceUsd = "$24.99",
            badgeText = "+30% BONUS"
        ),
        CoinPackage(
            id = "coins_350000",
            coins = 350000,
            bonusCoins = 100000,
            priceUsd = "$54.99",
            badgeText = "VIP EXCLUSIVE"
        ),
        CoinPackage(
            id = "coins_800000",
            coins = 800000,
            bonusCoins = 300000,
            priceUsd = "$109.99",
            badgeText = "LEGENDARY"
        )
    )

    val initialRooms = listOf(
        StreamRoom(
            id = "room_dj_1",
            title = "🔥 Electric Pulse: DJ Neon Echo Live Weekend Club!",
            hostName = "DJ Neon Echo",
            hostHandle = "@neonecho",
            hostAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            hostBio = "International EDM producer & DJ. Dropping unreleased festival beats!",
            coverDrawableName = "stream_dj_neon_1791962557863",
            viewerCount = 34780,
            totalDiamonds = 248900L,
            roomType = RoomType.SOLO_LIVE,
            tags = listOf("EDM", "Club", "Festival", "Top Rank"),
            category = "Music & DJ"
        ),
        StreamRoom(
            id = "room_pk_1",
            title = "⚔️ Acoustic Jam vs Jazz Queen - Live PK Battle Round 3!",
            hostName = "Leo Carter",
            hostHandle = "@leomusic",
            hostAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            hostBio = "Acoustic singer & songwriter. Defending the champion belt tonight!",
            coverDrawableName = "stream_singer_1791962591600",
            viewerCount = 12450,
            totalDiamonds = 96400L,
            roomType = RoomType.PK_BATTLE,
            tags = listOf("PK Battle", "Singing", "Live Acoustic"),
            category = "PK Battles",
            pkOpponent = PkOpponent(
                hostName = "Elena Voss",
                hostHandle = "@elenavoss",
                hostAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                initialScore = 1840
            )
        ),
        StreamRoom(
            id = "room_party_1",
            title = "🎉 VIP Lounge & Open Mic Party (8 Seats Available)",
            hostName = "Luna Star",
            hostHandle = "@lunastar",
            hostAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
            hostBio = "Party room host! Jump in the mic seats, tell stories, play dice!",
            coverDrawableName = "party_coins_banner_1791962627817",
            viewerCount = 18920,
            totalDiamonds = 168200L,
            roomType = RoomType.MULTI_GUEST_PARTY,
            tags = listOf("Multi-Seat", "Party Talk", "Chill", "VIP"),
            category = "Party Rooms"
        ),
        StreamRoom(
            id = "room_talent_2",
            title = "🎸 Midnight Guitar Solo & Request Line",
            hostName = "Zack Woods",
            hostHandle = "@zackguitar",
            hostAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            hostBio = "Rock guitarist taking viewer song requests. Big gift perks!",
            coverDrawableName = "stream_singer_1791962591600",
            viewerCount = 5420,
            totalDiamonds = 34500L,
            roomType = RoomType.SOLO_LIVE,
            tags = listOf("Guitar", "Rock", "Requests"),
            category = "Music & DJ"
        ),
        StreamRoom(
            id = "room_party_2",
            title = "🎲 Game Night Party: Truth or Dare & Dice Roll!",
            hostName = "Mika & Squad",
            hostHandle = "@mikaparty",
            hostAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
            hostBio = "Wild game party room! Grab a seat and challenge the room!",
            coverDrawableName = "party_coins_banner_1791962627817",
            viewerCount = 9200,
            totalDiamonds = 82100L,
            roomType = RoomType.MULTI_GUEST_PARTY,
            tags = listOf("Party Games", "Dice", "Fun", "Hangout"),
            category = "Party Rooms"
        )
    )

    fun getInitialPartySeats(): List<PartySeat> {
        return listOf(
            PartySeat(0, UserProfile("u1", "Maya Ray", "@mayaray", "https://images.unsplash.com/photo-1494790108377-be9c29b29330", 14, "VIP 2"), false, true),
            PartySeat(1, UserProfile("u2", "Dave_Beat", "@davebeat", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", 9, "VIP 1"), false, false),
            PartySeat(2, UserProfile("u3", "Sarah K", "@sarahk", "https://images.unsplash.com/photo-1534528741775-53994a69daeb", 22, "VIP 4"), false, true),
            PartySeat(3, null),
            PartySeat(4, UserProfile("u4", "Kenji", "@kenjix", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", 11, "VIP 1"), true, false),
            PartySeat(5, null),
            PartySeat(6, null),
            PartySeat(7, null)
        )
    }

    val initialChatMessages = listOf(
        ChatMessage("m1", "System", "Welcome to the live stream! Please adhere to community guidelines.", 0, null, true),
        ChatMessage("m2", "Alex_VIP", "This drop is insane!! 🔥🔥🔥", 18, "VIP 3"),
        ChatMessage("m3", "Lily99", "Loving the vibes tonight! ✨", 7, null),
        ChatMessage("m4", "JordanX", "Sent 10x Neon Rose! Keep killing it!", 25, "Top Gifter", giftTransaction = GiftTransaction(
            "gt1", "JordanX", "", "DJ Neon Echo", sampleGifts[0], 10
        )),
        ChatMessage("m5", "CyberNova", "Hello from Tokyo! 🗼⚡", 15, "VIP 2")
    )

    val topStreamersLeaderboard = listOf(
        LeaderboardItem(1, "DJ Neon Echo", "@neonecho", 248900L, "https://images.unsplash.com/photo-1494790108377-be9c29b29330", 35, "👑 Champion"),
        LeaderboardItem(2, "Luna Star", "@lunastar", 168200L, "https://images.unsplash.com/photo-1517841905240-472988babdf9", 31, "🥈 Master"),
        LeaderboardItem(3, "Leo Carter", "@leomusic", 96400L, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", 28, "🥉 Elite"),
        LeaderboardItem(4, "Elena Voss", "@elenavoss", 82100L, "https://images.unsplash.com/photo-1534528741775-53994a69daeb", 24, "Diamond"),
        LeaderboardItem(5, "ApexKai", "@apexkai", 64300L, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", 20, "Platinum")
    )

    val topGiftersLeaderboard = listOf(
        LeaderboardItem(1, "KingWhale_01", "@kingwhale", 520000L, "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde", 45, "👑 Legend Gifter"),
        LeaderboardItem(2, "JordanX", "@jordanx", 310500L, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", 38, "🥈 Grand Patron"),
        LeaderboardItem(3, "Bella_Luxe", "@bellaluxe", 185000L, "https://images.unsplash.com/photo-1494790108377-be9c29b29330", 32, "🥉 Royal VIP"),
        LeaderboardItem(4, "CyberNova", "@cybernova", 98000L, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", 26, "Gold Patron"),
        LeaderboardItem(5, "StarGazer", "@stargazer", 42000L, "https://images.unsplash.com/photo-1534528741775-53994a69daeb", 18, "Silver Patron")
    )

    val simulatedUserComments = listOf(
        "🔥🔥 This stream is pure fire!",
        "Let's goooo! Send those gifts! 🚀",
        "Can you play that synth track again? 🎵",
        "W song choice, big W! 👏",
        "Greetings to everyone in the party room!",
        "Drop a heart combo everyone! 💖💖",
        "Winner of PK battle for sure!",
        "Sent a supercar! Zoom zoom! 🏎️💨",
        "The sound quality is crisp! 🎧",
        "Who is on seat 3? You sound great!",
        "VIP hype train incoming! 🚂✨"
    )

    val sampleReels = listOf(
        ReelItem(
            id = "reel_1",
            creatorName = "DJ Neon Echo",
            creatorHandle = "@neonecho",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            coverDrawableName = "stream_dj_neon_1791962557863",
            caption = "Drop it loud! Festival crowd energy at 2AM 🔊⚡ What's your favorite drop?",
            musicTrack = "DJ Neon Echo • Midnight Pulse Club Mix",
            likesCount = 48200,
            commentsCount = 1380,
            giftsDiamondCount = 92400L,
            isLiked = false,
            tags = listOf("Festival", "EDM", "Club", "LiveParty")
        ),
        ReelItem(
            id = "reel_2",
            creatorName = "Leo Carter",
            creatorHandle = "@leomusic",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            coverDrawableName = "stream_singer_1791962591600",
            caption = "Late night acoustic session 🎸 Sing along with me! Dedicated to all top gifters ❤️",
            musicTrack = "Leo Carter • Acoustic Sunset (Original Live)",
            likesCount = 31400,
            commentsCount = 890,
            giftsDiamondCount = 56200L,
            isLiked = true,
            tags = listOf("Acoustic", "Singer", "LiveVocals")
        ),
        ReelItem(
            id = "reel_3",
            creatorName = "Luna Star",
            creatorHandle = "@lunastar",
            creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
            coverDrawableName = "party_coins_banner_1791962627817",
            caption = "Celebration gala finale! Mythic Dragon gift reaction was UNREAL! 🐉✨🏆",
            musicTrack = "LumiLive Party • Carnival Celebration Anthem",
            likesCount = 64100,
            commentsCount = 2950,
            giftsDiamondCount = 188000L,
            isLiked = false,
            tags = listOf("PKBattle", "Gifts", "Dragon", "VIP")
        )
    )

    val cameraFilters = listOf(
        CameraFilter(
            id = "filter_none",
            name = "Original",
            emoji = "✨",
            tintColorHex = 0x00000000,
            description = "Natural clean studio look"
        ),
        CameraFilter(
            id = "filter_cyber",
            name = "Cyber Neon",
            emoji = "⚡",
            tintColorHex = 0x3300F0FF,
            description = "Electric cyan & magenta club aura"
        ),
        CameraFilter(
            id = "filter_sunset",
            name = "Sunset Glow",
            emoji = "🌅",
            tintColorHex = 0x33FF5722,
            description = "Warm golden hour amber tones"
        ),
        CameraFilter(
            id = "filter_rose",
            name = "Rose Velvet",
            emoji = "🌸",
            tintColorHex = 0x33FF2A85,
            description = "Glamorous soft pink radiance"
        ),
        CameraFilter(
            id = "filter_gold",
            name = "Golden VIP",
            emoji = "👑",
            tintColorHex = 0x33FFD700,
            description = "Sparkling gold prestige luster"
        ),
        CameraFilter(
            id = "filter_matrix",
            name = "Emerald Vibe",
            emoji = "💚",
            tintColorHex = 0x3300E676,
            description = "Vivid green stage lighting"
        ),
        CameraFilter(
            id = "filter_midnight",
            name = "Midnight Deep",
            emoji = "🌙",
            tintColorHex = 0x444A148C,
            description = "Moody dark violet atmosphere"
        )
    )

    val sampleReelComments = listOf(
        ReelComment("rc1", "Alex_VIP", "This drop gave me chills!! 🎧🔥", "2m ago", 42),
        ReelComment("rc2", "Mia_Styles", "The lighting in this stream is unmatched! ✨", "5m ago", 19),
        ReelComment("rc3", "JordanX", "Just dropped 5x supercars in your stream! Legend! 🏎️💨", "12m ago", 84),
        ReelComment("rc4", "CyberSam", "Can we get this on replay?? W stream!", "18m ago", 12),
        ReelComment("rc5", "Luna_Fan", "Best content on LumiLive right now! ❤️❤️", "35m ago", 6)
    )
}

