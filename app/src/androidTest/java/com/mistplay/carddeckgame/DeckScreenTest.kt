package com.mistplay.carddeckgame

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the Deck screen. Requires device/emulator with network for full flow.
 */
@RunWith(AndroidJUnit4::class)
class DeckScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_showsCardDeckTitle() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Card Deck").assertExists()
    }

    @Test
    fun initialScreen_showsShuffleDeckButton() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Shuffle deck").assertExists()
    }

    @Test
    fun initialScreen_showsDrawnCardsLabel() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Drawn cards").assertExists()
    }

    @Test
    fun clickShuffleDeck_startsLoadingThenShowsRemaining() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Shuffle deck").performClick()
        // Wait for network: success shows "Remaining" (in "Remaining: 52") or button becomes "New deck"
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            try {
                composeTestRule.onNodeWithText("Remaining").assertExists()
                true
            } catch (_: Throwable) {
                try {
                    composeTestRule.onNodeWithText("New deck").assertExists()
                    true
                } catch (_: Throwable) {
                    false
                }
            }
        }
    }
}
