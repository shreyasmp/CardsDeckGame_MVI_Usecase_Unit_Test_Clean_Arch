package com.mistplay.carddeckgame.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao
import com.mistplay.carddeckgame.core.database.entity.DrawnCardEntity
import com.mistplay.carddeckgame.core.database.entity.GameSessionEntity

@Database(
    entities = [GameSessionEntity::class, DrawnCardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun drawnCardDao(): DrawnCardDao
}
