package com.mistplay.carddeckgame.di

import com.mistplay.carddeckgame.core.database.AppDatabase
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao
import com.mistplay.carddeckgame.core.network.DeckApiProvider
import com.mistplay.carddeckgame.core.network.api.DeckOfCardsApi
import com.mistplay.carddeckgame.feature.deck.DeckFeature
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { DeckApiProvider.createOkHttpClient() }
    single { DeckApiProvider.createRetrofit(get()) }
    single<DeckOfCardsApi> { DeckApiProvider.createApi(get()) }
    single {
        androidx.room.Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "card_deck_db"
        ).build()
    }
    single<GameSessionDao> { get<AppDatabase>().gameSessionDao() }
    single<DrawnCardDao> { get<AppDatabase>().drawnCardDao() }
}

fun allModules() = listOf(appModule, DeckFeature.koinModule)
