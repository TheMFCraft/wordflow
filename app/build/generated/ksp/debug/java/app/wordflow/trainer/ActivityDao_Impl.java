package app.wordflow.trainer;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
public final class ActivityDao_Impl implements ActivityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ActivityEntity> __insertionAdapterOfActivityEntity;

  public ActivityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActivityEntity = new EntityInsertionAdapter<ActivityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `activity` (`id`,`langId`,`title`,`words`,`accuracy`,`date`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActivityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getLangId());
        statement.bindString(3, entity.getTitle());
        statement.bindLong(4, entity.getWords());
        statement.bindLong(5, entity.getAccuracy());
        statement.bindString(6, entity.getDate());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insert(final ActivityEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfActivityEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ActivityEntity>> observe() {
    final String _sql = "SELECT * FROM activity ORDER BY createdAt DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activity"}, new Callable<List<ActivityEntity>>() {
      @Override
      @NonNull
      public List<ActivityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLangId = CursorUtil.getColumnIndexOrThrow(_cursor, "langId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfWords = CursorUtil.getColumnIndexOrThrow(_cursor, "words");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ActivityEntity> _result = new ArrayList<ActivityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpLangId;
            _tmpLangId = _cursor.getString(_cursorIndexOfLangId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpWords;
            _tmpWords = _cursor.getInt(_cursorIndexOfWords);
            final int _tmpAccuracy;
            _tmpAccuracy = _cursor.getInt(_cursorIndexOfAccuracy);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ActivityEntity(_tmpId,_tmpLangId,_tmpTitle,_tmpWords,_tmpAccuracy,_tmpDate,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
