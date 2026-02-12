package com.mistplay.carddeckgame.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mistplay.carddeckgame.core.database.entity.DrawnCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawnCardDao {

    @Insert
    suspend fun insertAll(cards: List<DrawnCardEntity>)

    @Insert
    suspend fun insert(card: DrawnCardEntity)

    @Query("SELECT * FROM drawn_cards WHERE sessionId = :sessionId ORDER BY drawnAt DESC")
    fun getCardsForSession(sessionId: Long): Flow<List<DrawnCardEntity>>

    @Query("SELECT * FROM drawn_cards WHERE sessionId = :sessionId ORDER BY drawnAt DESC")
    suspend fun getCardsForSessionOnce(sessionId: Long): List<DrawnCardEntity>

    @Query("DELETE FROM drawn_cards WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)
}
