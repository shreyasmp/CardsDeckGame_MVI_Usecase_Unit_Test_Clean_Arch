package com.mistplay.carddeckgame.feature.deck

import androidx.compose.runtime.Composable
import com.mistplay.carddeckgame.feature.deck.di.deckModule
import com.mistplay.carddeckgame.feature.deck.presentation.DeckScreen
import org.koin.core.module.Module

/**
 * Public API for the Deck feature. Exposes only what is required for the host app.
 * Can be reused in other games (e.g. Blackjack, Solitaire) by including this module.
 */
object DeckFeature {

    val koinModule: Module get() = deckModule

    @Suppress("FunctionNaming")
    @Composable
    fun DeckRoute() {
        DeckScreen()
    }
}
