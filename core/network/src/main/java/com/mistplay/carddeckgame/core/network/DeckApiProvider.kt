package com.mistplay.carddeckgame.core.network

import com.mistplay.carddeckgame.core.network.api.DeckOfCardsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DeckApiProvider {

    private const val BASE_URL = "https://deckofcardsapi.com/"

    fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    fun createRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createApi(retrofit: Retrofit): DeckOfCardsApi =
        retrofit.create(DeckOfCardsApi::class.java)
}
