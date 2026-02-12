package com.mistplay.carddeckgame.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mistplay.carddeckgame.core.database.entity.DrawnCardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DrawnCardDao_Impl implements DrawnCardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DrawnCardEntity> __insertionAdapterOfDrawnCardEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForSession;

  public DrawnCardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDrawnCardEntity = new EntityInsertionAdapter<DrawnCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `drawn_cards` (`id`,`sessionId`,`code`,`imageUrl`,`value`,`suit`,`drawnAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DrawnCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindString(3, entity.getCode());
        statement.bindString(4, entity.getImageUrl());
        statement.bindString(5, entity.getValue());
        statement.bindString(6, entity.getSuit());
        statement.bindLong(7, entity.getDrawnAt());
      }
    };
    this.__preparedStmtOfDeleteForSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM drawn_cards WHERE sessionId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<DrawnCardEntity> cards,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDrawnCardEntity.insert(cards);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final DrawnCardEntity card, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDrawnCardEntity.insert(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForSession(final long sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForSession.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, sessionId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteForSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DrawnCardEntity>> getCardsForSession(final long sessionId) {
    final String _sql = "SELECT * FROM drawn_cards WHERE sessionId = ? ORDER BY drawnAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drawn_cards"}, new Callable<List<DrawnCardEntity>>() {
      @Override
      @NonNull
      public List<DrawnCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfSuit = CursorUtil.getColumnIndexOrThrow(_cursor, "suit");
          final int _cursorIndexOfDrawnAt = CursorUtil.getColumnIndexOrThrow(_cursor, "drawnAt");
          final List<DrawnCardEntity> _result = new ArrayList<DrawnCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DrawnCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpValue;
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
            final String _tmpSuit;
            _tmpSuit = _cursor.getString(_cursorIndexOfSuit);
            final long _tmpDrawnAt;
            _tmpDrawnAt = _cursor.getLong(_cursorIndexOfDrawnAt);
            _item = new DrawnCardEntity(_tmpId,_tmpSessionId,_tmpCode,_tmpImageUrl,_tmpValue,_tmpSuit,_tmpDrawnAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCardsForSessionOnce(final long sessionId,
      final Continuation<? super List<DrawnCardEntity>> $completion) {
    final String _sql = "SELECT * FROM drawn_cards WHERE sessionId = ? ORDER BY drawnAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DrawnCardEntity>>() {
      @Override
      @NonNull
      public List<DrawnCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfSuit = CursorUtil.getColumnIndexOrThrow(_cursor, "suit");
          final int _cursorIndexOfDrawnAt = CursorUtil.getColumnIndexOrThrow(_cursor, "drawnAt");
          final List<DrawnCardEntity> _result = new ArrayList<DrawnCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DrawnCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpValue;
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
            final String _tmpSuit;
            _tmpSuit = _cursor.getString(_cursorIndexOfSuit);
            final long _tmpDrawnAt;
            _tmpDrawnAt = _cursor.getLong(_cursorIndexOfDrawnAt);
            _item = new DrawnCardEntity(_tmpId,_tmpSessionId,_tmpCode,_tmpImageUrl,_tmpValue,_tmpSuit,_tmpDrawnAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
