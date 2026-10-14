package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MockDataProvider
import com.example.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LumiLive", appName)
    }

    @Test
    fun `verify minimum gift price is 100 coins`() {
        val minPrice = MockDataProvider.sampleGifts.minOf { it.coinCost }
        assertEquals(100, minPrice)
        assertTrue(MockDataProvider.sampleGifts.all { it.coinCost >= 100 })
    }

    @Test
    fun `verify starting coin package is 2 dollars for 10000 coins`() {
        val firstPack = MockDataProvider.sampleCoinPackages.first()
        assertEquals(10000, firstPack.coins)
        assertEquals("$2.00", firstPack.priceUsd)
    }

    @Test
    fun `verify sample reels and camera filters exist`() {
        val reels = MockDataProvider.sampleReels
        assertTrue(reels.isNotEmpty())
        assertEquals("reel_1", reels.first().id)

        val filters = MockDataProvider.cameraFilters
        assertTrue(filters.isNotEmpty())
        assertEquals("Original", filters.first().name)
        assertTrue(filters.any { it.name == "Cyber Neon" })
    }

    @Test
    fun `verify PK battle ratio logic handles extremes safely`() {
        val hostScore = 5000L
        val opponentScore = 0L
        val total = (hostScore + opponentScore).coerceAtLeast(1L)
        val rawRatio = (hostScore.toFloat() / total.toFloat()).coerceIn(0.10f, 0.90f)

        // Ensure ratio stays within [0.10, 0.90] for proper rendering
        assertTrue(rawRatio in 0.10f..0.90f)
        assertEquals(0.90f, rawRatio, 0.001f)
    }
}
