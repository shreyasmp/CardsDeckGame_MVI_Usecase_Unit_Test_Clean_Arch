package com.mistplay.carddeckgame.feature.deck.presentation

import com.mistplay.carddeckgame.feature.deck.domain.Card

data class DeckState(
    val deckId: String? = null,
    val remaining: Int = 0,
    val drawnCards: List<Card> = emptyList(),
    val selectedCard: Card? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Deck-only UI state. Split out so composables that only need deck info (and error)
 * don't recompose when [drawnCards] or [selectedCard] change.
 */
data class DeckUiState(
    val deckId: String? = null,
    val remaining: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
