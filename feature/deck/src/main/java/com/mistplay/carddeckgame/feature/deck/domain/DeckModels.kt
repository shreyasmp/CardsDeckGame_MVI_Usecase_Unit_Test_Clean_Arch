package com.mistplay.carddeckgame.feature.deck.domain

/**
 * Domain model for a card. Exposed to UI only; mapping from API/DB in repository.
 */
data class Card(
    val code: String,
    val imageUrl: String,
    val value: String,
    val suit: String
)

/**
 * Represents current deck session state from API.
 */
data class DeckSession(
    val deckId: String,
    val remaining: Int,
    val shuffled: Boolean
)

/**
 * Pile of cards (e.g. discard, hand). API creates piles on the fly by name.
 */
data class Pile(
    val name: String,
    val cards: List<Card>,
    val remaining: Int
)
