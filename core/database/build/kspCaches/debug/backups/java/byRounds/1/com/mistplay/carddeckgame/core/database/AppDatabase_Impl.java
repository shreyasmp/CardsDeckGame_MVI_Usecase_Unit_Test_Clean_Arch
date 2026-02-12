package com.mistplay.carddeckgame.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao;
import com.mistplay.carddeckgame.core.database.dao.DrawnCardDao_Impl;
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao;
import com.mistplay.carddeckgame.core.database.dao.GameSessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile GameSessionDao _gameSessionDao;

  private volatile DrawnCardDao _drawnCardDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `game_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deckId` TEXT NOT NULL, `remaining` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `drawn_cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `code` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `value` TEXT NOT NULL, `suit` TEXT NOT NULL, `drawnAt` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `game_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drawn_cards_sessionId` ON `drawn_cards` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e589d351896e0f521cfa8b7f56a25ecc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `game_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `drawn_cards`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsGameSessions = new HashMap<String, TableInfo.Column>(4);
        _columnsGameSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameSessions.put("deckId", new TableInfo.Column("deckId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameSessions.put("remaining", new TableInfo.Column("remaining", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGameSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGameSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGameSessions = new TableInfo("game_sessions", _columnsGameSessions, _foreignKeysGameSessions, _indicesGameSessions);
        final TableInfo _existingGameSessions = TableInfo.read(db, "game_sessions");
        if (!_infoGameSessions.equals(_existingGameSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "game_sessions(com.mistplay.carddeckgame.core.database.entity.GameSessionEntity).\n"
                  + " Expected:\n" + _infoGameSessions + "\n"
                  + " Found:\n" + _existingGameSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsDrawnCards = new HashMap<String, TableInfo.Column>(7);
        _columnsDrawnCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("code", new TableInfo.Column("code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("suit", new TableInfo.Column("suit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrawnCards.put("drawnAt", new TableInfo.Column("drawnAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDrawnCards = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDrawnCards.add(new TableInfo.ForeignKey("game_sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDrawnCards = new HashSet<TableInfo.Index>(1);
        _indicesDrawnCards.add(new TableInfo.Index("index_drawn_cards_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoDrawnCards = new TableInfo("drawn_cards", _columnsDrawnCards, _foreignKeysDrawnCards, _indicesDrawnCards);
        final TableInfo _existingDrawnCards = TableInfo.read(db, "drawn_cards");
        if (!_infoDrawnCards.equals(_existingDrawnCards)) {
          return new RoomOpenHelper.ValidationResult(false, "drawn_cards(com.mistplay.carddeckgame.core.database.entity.DrawnCardEntity).\n"
                  + " Expected:\n" + _infoDrawnCards + "\n"
                  + " Found:\n" + _existingDrawnCards);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e589d351896e0f521cfa8b7f56a25ecc", "6aa4f95bec9861c88ccbb99dee61cfee");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "game_sessions","drawn_cards");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `game_sessions`");
      _db.execSQL("DELETE FROM `drawn_cards`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(GameSessionDao.class, GameSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DrawnCardDao.class, DrawnCardDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public GameSessionDao gameSessionDao() {
    if (_gameSessionDao != null) {
      return _gameSessionDao;
    } else {
      synchronized(this) {
        if(_gameSessionDao == null) {
          _gameSessionDao = new GameSessionDao_Impl(this);
        }
        return _gameSessionDao;
      }
    }
  }

  @Override
  public DrawnCardDao drawnCardDao() {
    if (_drawnCardDao != null) {
      return _drawnCardDao;
    } else {
      synchronized(this) {
        if(_drawnCardDao == null) {
          _drawnCardDao = new DrawnCardDao_Impl(this);
        }
        return _drawnCardDao;
      }
    }
  }
}
