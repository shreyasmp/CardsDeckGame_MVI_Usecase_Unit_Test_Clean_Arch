package com.mistplay.carddeckgame.feature.deck.data

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao
import com.mistplay.carddeckgame.core.database.entity.DrawnCardEntity
import com.mistplay.carddeckgame.core.database.entity.GameSessionEntity
import com.mistplay.carddeckgame.core.network.api.CardDto
import com.mistplay.carddeckgame.core.network.api.DeckOfCardsApi
import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.domain.Pile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeckRepository(
    private val api: DeckOfCardsApi,
    private val gameSessionDao: GameSessionDao,
    private val drawnCardDao: DrawnCardDao
) {

    suspend fun shuffle(deckCount: Int = 1): Result<DeckSession> = withContext(Dispatchers.IO) {
        try {
            val response = api.shuffle(deckCount)
            if (!response.success) return@withContext Result.Error("Shuffle failed")
            val session = DeckSession(
                deckId = response.deckId,
                remaining = response.remaining,
                shuffled = response.shuffled
            )
            gameSessionDao.insert(
                GameSessionEntity(deckId = response.deckId, remaining = response.remaining)
            )
            Result.Success(session)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error", e)
        }
    }

    suspend fun draw(deckId: String, count: Int): Result<Pair<DeckSession, List<Card>>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.draw(deckId, count)
                if (!response.success) return@withContext Result.Error("Draw failed")
                val session = DeckSession(deckId = response.deckId, response.remaining, shuffled = true)
                val cards = response.cards.map { it.toCard() }
                val sessionEntity = gameSessionDao.getLatestSession()
                sessionEntity?.let {
                    drawnCardDao.insertAll(
                        cards.map { c ->
                            DrawnCardEntity(
                                sessionId = it.id,
                                code = c.code,
                                imageUrl = c.imageUrl,
                                value = c.value,
                                suit = c.suit
                            )
                        }
                    )
                }
                Result.Success(session to cards)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error", e)
            }
        }

    suspend fun reshuffle(deckId: String, remainingOnly: Boolean = false): Result<DeckSession> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.reshuffle(deckId, remainingOnly)
                if (!response.success) return@withContext Result.Error("Reshuffle failed")
                Result.Success(
                    DeckSession(
                        deckId = response.deckId,
                        remaining = response.remaining,
                        shuffled = response.shuffled
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error", e)
            }
        }

    suspend fun addToPile(deckId: String, pileName: String, cardCodes: List<String>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.addToPile(deckId, pileName, cardCodes.joinToString(","))
                if (!response.success) return@withContext Result.Error("Add to pile failed")
                Result.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error", e)
            }
        }

    suspend fun listPile(deckId: String, pileName: String): Result<Pile> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.listPile(deckId, pileName)
                if (!response.success) return@withContext Result.Error("List pile failed")
                val pileEntry = response.piles?.get(pileName) ?: return@withContext Result.Success(
                    Pile(pileName, emptyList(), 0)
                )
                val cards = (pileEntry.cards ?: emptyList()).map { it.toCard() }
                val rem = pileEntry.remaining?.toIntOrNull() ?: 0
                Result.Success(Pile(pileName, cards, rem))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error", e)
            }
        }

    suspend fun returnToDeck(deckId: String): Result<DeckSession> = withContext(Dispatchers.IO) {
        try {
            val response = api.returnToDeck(deckId)
            if (!response.success) return@withContext Result.Error("Return failed")
            Result.Success(
                DeckSession(
                    deckId = response.deckId,
                    remaining = response.remaining,
                    shuffled = response.shuffled ?: false
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error", e)
        }
    }

    suspend fun loadLastDrawnCards(): List<Card> = withContext(Dispatchers.IO) {
        val session = gameSessionDao.getLatestSession() ?: return@withContext emptyList()
        drawnCardDao.getCardsForSessionOnce(session.id).map { e ->
            Card(e.code, e.imageUrl, e.value, e.suit)
        }
    }

    private fun CardDto.toCard() = Card(
        code = code,
        imageUrl = image,
        value = value,
        suit = suit
    )
}
