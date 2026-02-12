package com.mistplay.carddeckgame.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mistplay.carddeckgame.core.database.entity.GameSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: GameSessionEntity): Long

    @Query("SELECT * FROM game_sessions ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestSession(): GameSessionEntity?

    @Query("SELECT * FROM game_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Query("DELETE FROM game_sessions")
    suspend fun deleteAll()
}
