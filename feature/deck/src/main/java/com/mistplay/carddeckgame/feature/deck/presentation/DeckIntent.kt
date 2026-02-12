package com.mistplay.carddeckgame.feature.deck.presentation

import com.mistplay.carddeckgame.feature.deck.domain.Card

sealed class DeckIntent {
    data object ShuffleDeck : DeckIntent()
    data object ReshuffleDeck : DeckIntent()
    data class DrawCards(val count: Int = 1) : DeckIntent()
    data class CardClicked(val card: Card) : DeckIntent()
    data object DismissCardDetail : DeckIntent()
    data object Retry : DeckIntent()
    data object LoadLastSession : DeckIntent()
}
