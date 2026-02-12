package com.mistplay.carddeckgame.feature.deck.domain.usecase

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.data.DeckRepository

/**
 * Result of a successful draw: updated session and the full list of drawn cards (existing + new).
 * Business rule: new cards are appended to current drawn list.
 */
data class DrawCardsOutcome(
    val session: DeckSession,
    val drawnCards: List<Card>
)

class DrawCardsUseCase constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(
        deckId: String?,
        currentDrawnCards: List<Card>,
        count: Int
    ): Result<DrawCardsOutcome> {
        if (deckId == null) return Result.Error("No deck. Shuffle first.")
        return when (val result = repository.draw(deckId, count)) {
            is Result.Success -> {
                val (session, newCards) = result.data
                Result.Success(DrawCardsOutcome(session, currentDrawnCards + newCards))
            }
            is Result.Error -> result
            is Result.Loading -> result
        }
    }
}
