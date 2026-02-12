package com.mistplay.carddeckgame.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val remaining: Int,
    val createdAt: Long = System.currentTimeMillis()
)
