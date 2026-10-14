package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockDataProvider
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FlyingHeart(
    val id: Long,
    val xOffsetRatio: Float, // 0.0 to 1.0 relative
    val colorIndex: Int,
    val size: Float
)

class LiveStreamViewModel : ViewModel() {

    private val _rooms = MutableStateFlow(MockDataProvider.initialRooms)
    val rooms: StateFlow<List<StreamRoom>> = _rooms.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _currentUser = MutableStateFlow(MockDataProvider.defaultUser)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _activeRoom = MutableStateFlow<StreamRoom?>(null)
    val activeRoom: StateFlow<StreamRoom?> = _activeRoom.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _partySeats = MutableStateFlow<List<PartySeat>>(MockDataProvider.getInitialPartySeats())
    val partySeats: StateFlow<List<PartySeat>> = _partySeats.asStateFlow()

    private val _mySeatIndex = MutableStateFlow<Int?>(null)
    val mySeatIndex: StateFlow<Int?> = _mySeatIndex.asStateFlow()

    private val _isMyMicMuted = MutableStateFlow(false)
    val isMyMicMuted: StateFlow<Boolean> = _isMyMicMuted.asStateFlow()

    private val _pkHostScore = MutableStateFlow(3200)
    val pkHostScore: StateFlow<Int> = _pkHostScore.asStateFlow()

    private val _pkOpponentScore = MutableStateFlow(2800)
    val pkOpponentScore: StateFlow<Int> = _pkOpponentScore.asStateFlow()

    private val _activeGiftAnimation = MutableStateFlow<GiftTransaction?>(null)
    val activeGiftAnimation: StateFlow<GiftTransaction?> = _activeGiftAnimation.asStateFlow()

    private val _flyingHearts = MutableStateFlow<List<FlyingHeart>>(emptyList())
    val flyingHearts: StateFlow<List<FlyingHeart>> = _flyingHearts.asStateFlow()

    private val _comboCounter = MutableStateFlow(0)
    val comboCounter: StateFlow<Int> = _comboCounter.asStateFlow()

    private val _recentGifterAlert = MutableStateFlow<String?>(null)
    val recentGifterAlert: StateFlow<String?> = _recentGifterAlert.asStateFlow()

    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    private val _soundEffectTrigger = MutableStateFlow<String?>(null)
    val soundEffectTrigger: StateFlow<String?> = _soundEffectTrigger.asStateFlow()

    private val _isBroadcasting = MutableStateFlow(false)
    val isBroadcasting: StateFlow<Boolean> = _isBroadcasting.asStateFlow()

    private val _reels = MutableStateFlow(MockDataProvider.sampleReels)
    val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

    private val _reelComments = MutableStateFlow(MockDataProvider.sampleReelComments)
    val reelComments: StateFlow<List<ReelComment>> = _reelComments.asStateFlow()

    private val _selectedCameraFilter = MutableStateFlow(MockDataProvider.cameraFilters[0])
    val selectedCameraFilter: StateFlow<CameraFilter> = _selectedCameraFilter.asStateFlow()

    private var simulatedStreamJob: Job? = null
    private var comboResetJob: Job? = null

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun joinRoom(room: StreamRoom) {
        _activeRoom.value = room
        _chatMessages.value = MockDataProvider.initialChatMessages
        _partySeats.value = MockDataProvider.getInitialPartySeats()
        _mySeatIndex.value = null
        _isMyMicMuted.value = false
        _flyingHearts.value = emptyList()
        _activeGiftAnimation.value = null

        if (room.roomType == RoomType.PK_BATTLE) {
            _pkHostScore.value = 4200
            _pkOpponentScore.value = room.pkOpponent?.initialScore ?: 3800
        }

        startSimulation()
    }

    fun leaveRoom() {
        simulatedStreamJob?.cancel()
        _activeRoom.value = null
        _mySeatIndex.value = null
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val user = _currentUser.value
        val msg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = user.name,
            message = text.trim(),
            userLevel = user.level,
            badgeType = user.vipTier
        )
        _chatMessages.update { (it + msg).takeLast(40) }
    }

    fun addFlyingHeart() {
        val id = System.currentTimeMillis() + Random.nextLong(1000)
        val heart = FlyingHeart(
            id = id,
            xOffsetRatio = Random.nextFloat(),
            colorIndex = Random.nextInt(5),
            size = Random.nextFloat() * 14f + 26f
        )
        _flyingHearts.update { (it + heart).takeLast(25) }

        // Increase host diamonds slightly for heart engagement
        _activeRoom.update { current ->
            current?.copy(totalDiamonds = current.totalDiamonds + 1)
        }
    }

    fun sendGift(gift: Gift, count: Int = 1): Boolean {
        val totalCost = gift.coinCost * count
        val user = _currentUser.value

        if (user.coinsBalance < totalCost) {
            _toastNotification.value = "Insufficient coins! Please top up your wallet."
            return false
        }

        // Deduct coins
        _currentUser.update { it.copy(coinsBalance = it.coinsBalance - totalCost) }

        // Update Combo count
        comboResetJob?.cancel()
        _comboCounter.update { it + count }
        comboResetJob = viewModelScope.launch {
            delay(3500)
            _comboCounter.value = 0
        }

        val room = _activeRoom.value
        val transaction = GiftTransaction(
            id = "tx_${System.currentTimeMillis()}",
            senderName = user.name,
            senderAvatar = user.avatar,
            receiverName = room?.hostName ?: "Host",
            gift = gift,
            comboCount = _comboCounter.value
        )

        // Show Full Screen Animated Overlay
        _activeGiftAnimation.value = transaction
        _recentGifterAlert.value = "${user.name} sent ${gift.name} x$count! ✨"

        // Update Room Diamonds & PK Score
        val diamondsGained = totalCost * 10
        _activeRoom.update { current ->
            current?.copy(totalDiamonds = current.totalDiamonds + diamondsGained)
        }
        if (room?.roomType == RoomType.PK_BATTLE) {
            _pkHostScore.update { it + totalCost }
        }

        // Append to chat
        val chatMsg = ChatMessage(
            id = "gift_msg_${System.currentTimeMillis()}",
            senderName = user.name,
            message = "Sent ${gift.name} x$count ${gift.iconEmoji}",
            userLevel = user.level,
            badgeType = "Gifter",
            giftTransaction = transaction
        )
        _chatMessages.update { (it + chatMsg).takeLast(40) }

        // Hide overlay after animation duration
        viewModelScope.launch {
            delay(3000)
            if (_activeGiftAnimation.value?.id == transaction.id) {
                _activeGiftAnimation.value = null
            }
        }

        return true
    }

    fun takeSeat(seatIndex: Int) {
        val currentSeats = _partySeats.value.toMutableList()
        val myCurrent = _mySeatIndex.value

        if (myCurrent == seatIndex) {
            // Leave seat
            currentSeats[seatIndex] = PartySeat(seatIndex, null, isMuted = false, isSpeaking = false)
            _mySeatIndex.value = null
            _toastNotification.value = "You left seat #${seatIndex + 1}"
        } else {
            // If already in another seat, vacate it
            if (myCurrent != null) {
                currentSeats[myCurrent] = PartySeat(myCurrent, null)
            }
            // Take this seat
            currentSeats[seatIndex] = PartySeat(
                seatIndex = seatIndex,
                user = _currentUser.value,
                isMuted = _isMyMicMuted.value,
                isSpeaking = true
            )
            _mySeatIndex.value = seatIndex
            _toastNotification.value = "You are now on party seat #${seatIndex + 1}! 🎙️"
        }
        _partySeats.value = currentSeats
    }

    fun toggleMyMic() {
        val newMuted = !_isMyMicMuted.value
        _isMyMicMuted.value = newMuted
        val mySeat = _mySeatIndex.value
        if (mySeat != null) {
            val seats = _partySeats.value.toMutableList()
            seats[mySeat] = seats[mySeat].copy(isMuted = newMuted, isSpeaking = !newMuted)
            _partySeats.value = seats
        }
    }

    fun triggerPartySound(sound: String) {
        _soundEffectTrigger.value = sound
        val msg = ChatMessage(
            id = "sound_${System.currentTimeMillis()}",
            senderName = "Room SFX",
            message = "🔊 Sound played: $sound",
            userLevel = 0,
            isSystem = true
        )
        _chatMessages.update { (it + msg).takeLast(40) }

        viewModelScope.launch {
            delay(2000)
            if (_soundEffectTrigger.value == sound) {
                _soundEffectTrigger.value = null
            }
        }
    }

    fun buyCoinPackage(pack: CoinPackage) {
        val totalAdded = pack.coins + pack.bonusCoins
        _currentUser.update { current ->
            current.copy(coinsBalance = current.coinsBalance + totalAdded)
        }
        _toastNotification.value = "Successfully purchased ${pack.coins} (+${pack.bonusCoins} Bonus) Coins for ${pack.priceUsd}! 🪙"
    }

    fun convertDiamondsToCoins(diamonds: Int) {
        val currentDiamonds = _currentUser.value.diamondsEarned
        if (diamonds > currentDiamonds) {
            _toastNotification.value = "Not enough diamonds to convert!"
            return
        }
        val coinsGained = diamonds / 10
        _currentUser.update {
            it.copy(
                diamondsEarned = it.diamondsEarned - diamonds,
                coinsBalance = it.coinsBalance + coinsGained
            )
        }
        _toastNotification.value = "Converted $diamonds 💎 into $coinsGained 🪙 Coins!"
    }

    fun followHost(handle: String) {
        _currentUser.update {
            it.copy(followingCount = it.followingCount + 1)
        }
        _toastNotification.value = "Followed $handle! You'll be notified when they go live."
    }

    fun startBroadcasting(title: String, category: String, roomType: RoomType) {
        val newRoom = StreamRoom(
            id = "my_live_${System.currentTimeMillis()}",
            title = title.ifBlank { "Live Studio Party ✨" },
            hostName = _currentUser.value.name,
            hostHandle = _currentUser.value.handle,
            hostAvatar = _currentUser.value.avatar,
            hostBio = "Official Live Broadcast Studio",
            coverDrawableName = "stream_dj_neon_1791962557863",
            viewerCount = 1,
            totalDiamonds = 0,
            roomType = roomType,
            tags = listOf("Broadcast", "Live", category),
            category = category
        )
        _rooms.update { listOf(newRoom) + it }
        _isBroadcasting.value = true
        joinRoom(newRoom)
    }

    fun clearToast() {
        _toastNotification.value = null
    }

    fun selectCameraFilter(filter: CameraFilter) {
        _selectedCameraFilter.value = filter
        _toastNotification.value = "Filter applied: ${filter.name} ${filter.emoji}"
    }

    fun toggleLikeReel(reelId: String) {
        _reels.update { currentList ->
            currentList.map { reel ->
                if (reel.id == reelId) {
                    val newLiked = !reel.isLiked
                    val newLikes = if (newLiked) reel.likesCount + 1 else reel.likesCount - 1
                    reel.copy(isLiked = newLiked, likesCount = newLikes)
                } else reel
            }
        }
    }

    fun addReelComment(text: String) {
        if (text.isBlank()) return
        val user = _currentUser.value
        val comment = ReelComment(
            id = "rc_${System.currentTimeMillis()}",
            authorName = user.name,
            text = text.trim(),
            timeAgo = "Just now",
            likes = 0
        )
        _reelComments.update { listOf(comment) + it }
        _toastNotification.value = "Comment posted! 💬"
    }

    fun giftReel(reelId: String, gift: Gift, count: Int = 1): Boolean {
        val totalCost = gift.coinCost * count
        val user = _currentUser.value
        if (user.coinsBalance < totalCost) {
            _toastNotification.value = "Insufficient coins! Please top up your wallet."
            return false
        }

        // Deduct coins
        _currentUser.update { it.copy(coinsBalance = it.coinsBalance - totalCost) }

        // Update Reel gifts diamond count
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    reel.copy(giftsDiamondCount = reel.giftsDiamondCount + (totalCost * 10))
                } else reel
            }
        }

        val tx = GiftTransaction(
            id = "tx_${System.currentTimeMillis()}",
            senderName = user.name,
            senderAvatar = user.avatar,
            receiverName = "Creator",
            gift = gift,
            comboCount = count
        )
        _activeGiftAnimation.value = tx
        _toastNotification.value = "Sent ${gift.name} x$count to creator! ✨"

        viewModelScope.launch {
            delay(2800)
            if (_activeGiftAnimation.value?.id == tx.id) {
                _activeGiftAnimation.value = null
            }
        }
        return true
    }

    fun postNewReel(caption: String, music: String, filterName: String) {
        val user = _currentUser.value
        val newReel = ReelItem(
            id = "reel_${System.currentTimeMillis()}",
            creatorName = user.name,
            creatorHandle = user.handle,
            creatorAvatar = user.avatar,
            coverDrawableName = "stream_dj_neon_1791962557863",
            caption = caption.ifBlank { "New studio vibes recorded live on LumiLive! ✨🎥" },
            musicTrack = music.ifBlank { "Original Audio • ${user.name}" },
            likesCount = 1,
            commentsCount = 0,
            giftsDiamondCount = 0L,
            isLiked = true,
            filterName = filterName,
            tags = listOf("NewReel", "CreatorStudio", "LumiLive")
        )
        _reels.update { listOf(newReel) + it }
        _toastNotification.value = "🎉 Reel published successfully to the Reels feed!"
    }


    private fun startSimulation() {
        simulatedStreamJob?.cancel()
        simulatedStreamJob = viewModelScope.launch {
            val commenters = listOf("Kira_X", "NeonRider", "Mochi_Queen", "Zenith", "PixelBoy", "Valkyrie", "GrooveMaster")
            while (isActive) {
                delay(Random.nextLong(2200, 4800))
                if (_activeRoom.value == null) break

                // Generate simulated chat message or gift
                val isGiftEvent = Random.nextInt(10) < 3
                if (isGiftEvent) {
                    val randomGift = MockDataProvider.sampleGifts.random()
                    val count = listOf(1, 5, 10).random()
                    val gifterName = commenters.random()
                    val tx = GiftTransaction(
                        id = "sim_tx_${System.currentTimeMillis()}",
                        senderName = gifterName,
                        senderAvatar = "",
                        receiverName = _activeRoom.value?.hostName ?: "Host",
                        gift = randomGift,
                        comboCount = count
                    )
                    val giftMsg = ChatMessage(
                        id = "sim_g_${System.currentTimeMillis()}",
                        senderName = gifterName,
                        message = "Sent ${randomGift.name} x$count ${randomGift.iconEmoji}",
                        userLevel = Random.nextInt(5, 30),
                        badgeType = if (randomGift.coinCost > 100) "Top Gifter" else null,
                        giftTransaction = tx
                    )
                    _chatMessages.update { (it + giftMsg).takeLast(40) }

                    // If it's a luxury or special FX gift, trigger brief screen animation
                    if (randomGift.category != GiftCategory.POPULAR && Random.nextBoolean()) {
                        _activeGiftAnimation.value = tx
                        delay(2800)
                        if (_activeGiftAnimation.value?.id == tx.id) {
                            _activeGiftAnimation.value = null
                        }
                    }

                    // Increment room diamonds
                    _activeRoom.update { it?.copy(totalDiamonds = it.totalDiamonds + (randomGift.coinCost * count * 10)) }

                    // Increment PK score if battle
                    if (_activeRoom.value?.roomType == RoomType.PK_BATTLE) {
                        if (Random.nextBoolean()) {
                            _pkHostScore.update { it + (randomGift.coinCost * count) }
                        } else {
                            _pkOpponentScore.update { it + (randomGift.coinCost * count) }
                        }
                    }
                } else {
                    val msgText = MockDataProvider.simulatedUserComments.random()
                    val name = commenters.random()
                    val chatMsg = ChatMessage(
                        id = "sim_m_${System.currentTimeMillis()}",
                        senderName = name,
                        message = msgText,
                        userLevel = Random.nextInt(3, 20),
                        badgeType = if (Random.nextInt(5) == 0) "VIP" else null
                    )
                    _chatMessages.update { (it + chatMsg).takeLast(40) }
                }

                // Periodic viewer fluctuation & floating hearts
                _activeRoom.update { current ->
                    current?.copy(viewerCount = (current.viewerCount + Random.nextInt(-3, 8)).coerceAtLeast(12))
                }
                if (Random.nextBoolean()) {
                    addFlyingHeart()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulatedStreamJob?.cancel()
    }
}
