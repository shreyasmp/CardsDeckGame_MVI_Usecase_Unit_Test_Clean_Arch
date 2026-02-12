package com.mistplay.carddeckgame.feature.deck.domain.usecase

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.data.DeckRepository

/**
 * Result of reshuffle. Business rule: reshuffle clears drawn cards (fresh deck state).
 */
data class ReshuffleOutcome(
    val session: DeckSession,
    val drawnCards: List<Card> = emptyList()
)

class ReshuffleDeckUseCase constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deckId: String?, remainingOnly: Boolean = false): Result<ReshuffleOutcome> {
        if (deckId == null) return Result.Error("No deck. Shuffle first.")
        return when (val result = repository.reshuffle(deckId, remainingOnly)) {
            is Result.Success -> Result.Success(ReshuffleOutcome(result.data, drawnCards = emptyList()))
            is Result.Error -> result
            is Result.Loading -> result
        }
    }
}
