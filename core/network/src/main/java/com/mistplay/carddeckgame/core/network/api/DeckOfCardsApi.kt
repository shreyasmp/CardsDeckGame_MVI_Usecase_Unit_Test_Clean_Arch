package com.mistplay.carddeckgame.core.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Deck of Cards API - https://deckofcardsapi.com/
 * Exposes only what's required for deck games (shuffle, draw, piles, return).
 */
interface DeckOfCardsApi {

    @GET("api/deck/new/shuffle/")
    suspend fun shuffle(
        @Query("deck_count") deckCount: Int = 1
    ): ShuffleResponse

    @GET("api/deck/{deckId}/shuffle/")
    suspend fun reshuffle(
        @Path("deckId") deckId: String,
        @Query("remaining") remainingOnly: Boolean = false
    ): ShuffleResponse

    @GET("api/deck/new/")
    suspend fun newDeck(): ShuffleResponse

    @GET("api/deck/{deckId}/draw/")
    suspend fun draw(
        @Path("deckId") deckId: String,
        @Query("count") count: Int = 1
    ): DrawResponse

    @GET("api/deck/{deckId}/pile/{pileName}/add/")
    suspend fun addToPile(
        @Path("deckId") deckId: String,
        @Path("pileName") pileName: String,
        @Query("cards") cards: String
    ): PileResponse

    @GET("api/deck/{deckId}/pile/{pileName}/list/")
    suspend fun listPile(
        @Path("deckId") deckId: String,
        @Path("pileName") pileName: String
    ): PileListResponse

    @GET("api/deck/{deckId}/pile/{pileName}/draw/")
    suspend fun drawFromPile(
        @Path("deckId") deckId: String,
        @Path("pileName") pileName: String,
        @Query("count") count: Int? = null,
        @Query("cards") cards: String? = null
    ): DrawResponse

    @GET("api/deck/{deckId}/pile/{pileName}/draw/random/")
    suspend fun drawRandomFromPile(
        @Path("deckId") deckId: String,
        @Path("pileName") pileName: String,
        @Query("count") count: Int = 1
    ): DrawResponse

    @GET("api/deck/{deckId}/return/")
    suspend fun returnToDeck(
        @Path("deckId") deckId: String
    ): ReturnResponse
}
