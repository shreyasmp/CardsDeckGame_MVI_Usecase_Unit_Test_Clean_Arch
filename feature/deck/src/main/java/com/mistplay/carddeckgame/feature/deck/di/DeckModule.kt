package com.mistplay.carddeckgame.feature.deck.di

import com.mistplay.carddeckgame.feature.deck.data.DeckRepository
import com.mistplay.carddeckgame.feature.deck.domain.usecase.DrawCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.LoadLastDrawnCardsUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ReshuffleDeckUseCase
import com.mistplay.carddeckgame.feature.deck.domain.usecase.ShuffleDeckUseCase
import com.mistplay.carddeckgame.feature.deck.presentation.DeckViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val deckModule = module {
    single { DeckRepository(get(), get(), get()) }
    factory { ShuffleDeckUseCase(get()) }
    factory { DrawCardsUseCase(get()) }
    factory { ReshuffleDeckUseCase(get()) }
    factory { LoadLastDrawnCardsUseCase(get()) }
    viewModel {
        DeckViewModel(
            shuffleDeck = get(),
            drawCards = get(),
            reshuffleDeck = get(),
            loadLastDrawnCards = get()
        )
    }
}
