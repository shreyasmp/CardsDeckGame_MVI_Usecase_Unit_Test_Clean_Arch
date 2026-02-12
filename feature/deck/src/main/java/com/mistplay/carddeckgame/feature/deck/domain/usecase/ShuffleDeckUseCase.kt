package com.mistplay.carddeckgame.feature.deck.domain.usecase

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.data.DeckRepository

class ShuffleDeckUseCase constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deckCount: Int = 1): Result<DeckSession> =
        repository.shuffle(deckCount)
}
