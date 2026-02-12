package com.mistplay.carddeckgame.feature.deck.presentation

import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.domain.usecase.DrawCardsOutcome
import com.mistplay.carddeckgame.feature.deck.domain.usecase.DrawCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.LoadLastDrawnCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ReshuffleDeckUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ShuffleDeckUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeckViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val shuffleDeck = mockk<ShuffleDeckUseCase>()
    private val drawCards = mockk<DrawCardsUseCase>()
    private val reshuffleDeck = mockk<ReshuffleDeckUseCase>()
    private val loadLastDrawnCards = mockk<LoadLastDrawnCardsUseCase>()
    private lateinit var viewModel: DeckViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { loadLastDrawnCards() } returns emptyList()
        viewModel = DeckViewModel(shuffleDeck, drawCards, reshuffleDeck, loadLastDrawnCards)
    }

    @Test
    fun initialState_hasNoDeckAndNoError() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(null, viewModel.state.value.deckId)
        assertEquals(0, viewModel.state.value.remaining)
        assertEquals(emptyList<Card>(), viewModel.state.value.drawnCards)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun shuffleDeck_success_updatesStateWithDeck() = runTest {
        val session = DeckSession("deck123", 52, true)
        coEvery { shuffleDeck(1) } returns Result.Success(session)

        viewModel.handle(DeckIntent.ShuffleDeck)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("deck123", viewModel.state.value.deckId)
        assertEquals(52, viewModel.state.value.remaining)
        assertEquals(false, viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun shuffleDeck_error_setsErrorMessage() = runTest {
        coEvery { shuffleDeck(1) } returns Result.Error("Network failed")

        viewModel.handle(DeckIntent.ShuffleDeck)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Network failed", viewModel.state.value.errorMessage)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun drawCards_withoutDeck_setsError() = runTest {
        coEvery { drawCards(null, emptyList(), 1) } returns Result.Error("No deck. Shuffle first.")

        viewModel.handle(DeckIntent.DrawCards(1))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No deck. Shuffle first.", viewModel.state.value.errorMessage)
    }

    @Test
    fun drawCards_withDeck_appendsCardsToState() = runTest {
        val session = DeckSession("deck1", 51, true)
        val card = Card("AS", "https://img.png", "ACE", "SPADES")
        coEvery { shuffleDeck(1) } returns Result.Success(DeckSession("deck1", 52, true))
        viewModel.handle(DeckIntent.ShuffleDeck)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { drawCards("deck1", emptyList(), 1) } returns Result.Success(
            DrawCardsOutcome(session, listOf(card))
        )
        viewModel.handle(DeckIntent.DrawCards(1))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.drawnCards.size)
        assertEquals("AS", viewModel.state.value.drawnCards.first().code)
        assertEquals(51, viewModel.state.value.remaining)
    }

    @Test
    fun cardClicked_setsSelectedCard() = runTest {
        val card = Card("KH", "https://img.png", "KING", "HEARTS")
        viewModel.handle(DeckIntent.CardClicked(card))

        assertEquals(card, viewModel.state.value.selectedCard)
    }

    @Test
    fun dismissCardDetail_clearsSelectedCard() = runTest {
        val card = Card("QH", "https://img.png", "QUEEN", "HEARTS")
        viewModel.handle(DeckIntent.CardClicked(card))
        viewModel.handle(DeckIntent.DismissCardDetail)

        assertNull(viewModel.state.value.selectedCard)
    }

    @Test
    fun reshuffleDeck_withoutDeck_setsError() = runTest {
        coEvery { reshuffleDeck(null, false) } returns Result.Error("No deck. Shuffle first.")

        viewModel.handle(DeckIntent.ReshuffleDeck)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No deck. Shuffle first.", viewModel.state.value.errorMessage)
    }
}
