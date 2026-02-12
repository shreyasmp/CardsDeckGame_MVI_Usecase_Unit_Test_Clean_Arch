package com.mistplay.carddeckgame.core.network.api

import com.google.gson.annotations.SerializedName

data class ShuffleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("shuffled") val shuffled: Boolean,
    @SerializedName("remaining") val remaining: Int
)

data class CardDto(
    @SerializedName("code") val code: String,
    @SerializedName("image") val image: String,
    @SerializedName("images") val images: CardImagesDto?,
    @SerializedName("value") val value: String,
    @SerializedName("suit") val suit: String
)

data class CardImagesDto(
    @SerializedName("svg") val svg: String,
    @SerializedName("png") val png: String
)

data class DrawResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("cards") val cards: List<CardDto>,
    @SerializedName("remaining") val remaining: Int
)

data class PileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("remaining") val remaining: Int,
    @SerializedName("piles") val piles: Map<String, PileInfoDto>?
)

data class PileInfoDto(
    @SerializedName("remaining") val remaining: Int
)

data class PileListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("remaining") val remaining: String?,
    @SerializedName("piles") val piles: Map<String, PileWithCardsDto>?
)

data class PileWithCardsDto(
    @SerializedName("cards") val cards: List<CardDto>?,
    @SerializedName("remaining") val remaining: String?
)

data class ReturnResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    @SerializedName("shuffled") val shuffled: Boolean?,
    @SerializedName("remaining") val remaining: Int,
    @SerializedName("piles") val piles: Map<String, PileInfoDto>?
)
