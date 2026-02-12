package com.mistplay.carddeckgame.feature.deck.data

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao
import com.mistplay.carddeckgame.core.database.entity.GameSessionEntity
import com.mistplay.carddeckgame.core.network.api.CardDto
import com.mistplay.carddeckgame.core.network.api.DeckOfCardsApi
import com.mistplay.carddeckgame.core.network.api.DrawResponse
import com.mistplay.carddeckgame.core.network.api.ShuffleResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckRepositoryTest {

    private val api = mockk<DeckOfCardsApi>()
    private val gameSessionDao = mockk<GameSessionDao>(relaxed = true)
    private val drawnCardDao = mockk<DrawnCardDao>(relaxed = true)
    private val repository = DeckRepository(api, gameSessionDao, drawnCardDao)

    @Test
    fun `shuffle success returns DeckSession`() = runTest {
        coEvery { api.shuffle(1) } returns ShuffleResponse(true, "deck1", true, 52)
        coEvery { gameSessionDao.insert(any()) } returns 1L

        val result = repository.shuffle(1)

        assertTrue(result is Result.Success)
        assertEquals("deck1", (result as Result.Success).data.deckId)
        assertEquals(52, result.data.remaining)
        coVerify { gameSessionDao.insert(any<GameSessionEntity>()) }
    }

    @Test
    fun `shuffle API failure returns Error`() = runTest {
        coEvery { api.shuffle(1) } returns ShuffleResponse(false, "", false, 0)

        val result = repository.shuffle(1)

        assertTrue(result is Result.Error)
        assertEquals("Shuffle failed", (result as Result.Error).message)
    }

    @Test
    fun `shuffle network exception returns Error`() = runTest {
        coEvery { api.shuffle(1) } throws RuntimeException("No network")

        val result = repository.shuffle(1)

        assertTrue(result is Result.Error)
        assertEquals("No network", (result as Result.Error).message)
    }

    @Test
    fun `draw success returns session and cards`() = runTest {
        val cardDto = CardDto("AS", "https://img.png", null, "ACE", "SPADES")
        coEvery { api.draw("deck1", 1) } returns DrawResponse(true, "deck1", listOf(cardDto), 51)
        coEvery { gameSessionDao.getLatestSession() } returns GameSessionEntity(1L, "deck1", 52)

        val result = repository.draw("deck1", 1)

        assertTrue(result is Result.Success)
        val (session, cards) = (result as Result.Success).data
        assertEquals("deck1", session.deckId)
        assertEquals(51, session.remaining)
        assertEquals(1, cards.size)
        assertEquals("AS", cards[0].code)
        assertEquals("ACE", cards[0].value)
    }
}
