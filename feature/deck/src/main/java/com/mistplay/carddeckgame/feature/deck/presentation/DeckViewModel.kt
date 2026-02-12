package com.mistplay.carddeckgame.feature.deck.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mistplay.carddeckgame.core.common.Result
import com.mistplay.carddeckgame.feature.deck.domain.Card
import com.mistplay.carddeckgame.feature.deck.domain.DeckSession
import com.mistplay.carddeckgame.feature.deck.domain.usecase.DrawCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.LoadLastDrawnCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ReshuffleDeckUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ShuffleDeckUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Thin ViewModel: delegates to use cases and maps results to UI state.
 * Exposes split StateFlows so the UI can subscribe only to what each composable needs,
 * avoiding full-screen recomposition when e.g. only [drawnCards] changes.
 */
class DeckViewModel(
    private val shuffleDeck: ShuffleDeckUseCase,
    private val drawCards: DrawCardsUseCase,
    private val reshuffleDeck: ReshuffleDeckUseCase,
    private val loadLastDrawnCards: LoadLastDrawnCardsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DeckState())

    /** Full state; use for tests or when you need everything. */
    val state: StateFlow<DeckState> = _state.asStateFlow()

    /** Deck area + error only. Recomposes only when deckId, remaining, isLoading, or errorMessage change. */
    val deckUiState: StateFlow<DeckUiState> = _state
        .map { DeckUiState(it.deckId, it.remaining, it.isLoading, it.errorMessage) }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, DeckUiState())

    /** Drawn cards list. Recomposes only when the list changes. */
    val drawnCards: StateFlow<List<Card>> = _state
        .map { it.drawnCards }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Selected card for detail dialog. Recomposes only when selection changes. */
    val selectedCard: StateFlow<Card?> = _state
        .map { it.selectedCard }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        loadLastSessionIfAny()
    }

    fun handle(intent: DeckIntent) {
        when (intent) {
            is DeckIntent.ShuffleDeck -> shuffle()
            is DeckIntent.ReshuffleDeck -> reshuffle()
            is DeckIntent.DrawCards -> draw(intent.count)
            is DeckIntent.CardClicked -> _state.update { it.copy(selectedCard = intent.card) }
            is DeckIntent.DismissCardDetail -> _state.update { it.copy(selectedCard = null) }
            is DeckIntent.Retry -> _state.update { it.copy(errorMessage = null) }
            is DeckIntent.LoadLastSession -> loadLastSessionIfAny()
        }
    }

    private fun loadLastSessionIfAny() {
        viewModelScope.launch {
            val cards = loadLastDrawnCards()
            if (cards.isNotEmpty()) {
                _state.update { it.copy(drawnCards = cards, errorMessage = null) }
            }
        }
    }

    private fun shuffle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = shuffleDeck(1)) {
                is Result.Success -> applySession(result.data)
                is Result.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                is Result.Loading -> { }
            }
        }
    }

    private fun reshuffle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = reshuffleDeck(_state.value.deckId, remainingOnly = false)) {
                is Result.Success -> {
                    applySession(result.data.session)
                    _state.update { it.copy(drawnCards = result.data.drawnCards) }
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                is Result.Loading -> { }
            }
        }
    }

    private fun draw(count: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = drawCards(_state.value.deckId, _state.value.drawnCards, count)) {
                is Result.Success -> {
                    val outcome = result.data
                    _state.update {
                        it.copy(
                            deckId = outcome.session.deckId,
                            remaining = outcome.session.remaining,
                            drawnCards = outcome.drawnCards,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                is Result.Loading -> { }
            }
        }
    }

    private fun applySession(session: DeckSession) {
        _state.update {
            it.copy(
                deckId = session.deckId,
                remaining = session.remaining,
                isLoading = false,
                errorMessage = null
            )
        }
    }
}
