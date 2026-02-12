package com.mistplay.carddeckgame.feature.deck.domain.usecase

import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.data.DeckRepository

class LoadLastDrawnCardsUseCase constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(): List<Card> =
        repository.loadLastDrawnCards()
}
