package app.wordflow.trainer;

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
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class WordDao_Impl implements WordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WordEntity> __insertionAdapterOfWordEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBox;

  public WordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWordEntity = new EntityInsertionAdapter<WordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `words` (`id`,`lang`,`word`,`translation`,`phonetic`,`pos`,`gender`,`example`,`exampleDe`,`box`,`source`,`chapterId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLang());
        statement.bindString(3, entity.getWord());
        statement.bindString(4, entity.getTranslation());
        statement.bindString(5, entity.getPhonetic());
        statement.bindString(6, entity.getPos());
        statement.bindString(7, entity.getGender());
        statement.bindString(8, entity.getExample());
        statement.bindString(9, entity.getExampleDe());
        statement.bindLong(10, entity.getBox());
        statement.bindString(11, entity.getSource());
        statement.bindString(12, entity.getChapterId());
      }
    };
    this.__preparedStmtOfUpdateBox = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE words SET box = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<WordEntity> words,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWordEntity.insert(words);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBox(final String id, final int box,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBox.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, box);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfUpdateBox.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WordEntity>> observeAll() {
    final String _sql = "SELECT * FROM words";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"words"}, new Callable<List<WordEntity>>() {
      @Override
      @NonNull
      public List<WordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfPhonetic = CursorUtil.getColumnIndexOrThrow(_cursor, "phonetic");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfExampleDe = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleDe");
          final int _cursorIndexOfBox = CursorUtil.getColumnIndexOrThrow(_cursor, "box");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfChapterId = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterId");
          final List<WordEntity> _result = new ArrayList<WordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLang;
            _tmpLang = _cursor.getString(_cursorIndexOfLang);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpPhonetic;
            _tmpPhonetic = _cursor.getString(_cursorIndexOfPhonetic);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpExampleDe;
            _tmpExampleDe = _cursor.getString(_cursorIndexOfExampleDe);
            final int _tmpBox;
            _tmpBox = _cursor.getInt(_cursorIndexOfBox);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpChapterId;
            _tmpChapterId = _cursor.getString(_cursorIndexOfChapterId);
            _item = new WordEntity(_tmpId,_tmpLang,_tmpWord,_tmpTranslation,_tmpPhonetic,_tmpPos,_tmpGender,_tmpExample,_tmpExampleDe,_tmpBox,_tmpSource,_tmpChapterId);
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
  public Flow<List<WordEntity>> observeByLang(final String lang) {
    final String _sql = "SELECT * FROM words WHERE lang = ? ORDER BY box ASC, word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lang);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"words"}, new Callable<List<WordEntity>>() {
      @Override
      @NonNull
      public List<WordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfPhonetic = CursorUtil.getColumnIndexOrThrow(_cursor, "phonetic");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfExampleDe = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleDe");
          final int _cursorIndexOfBox = CursorUtil.getColumnIndexOrThrow(_cursor, "box");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfChapterId = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterId");
          final List<WordEntity> _result = new ArrayList<WordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLang;
            _tmpLang = _cursor.getString(_cursorIndexOfLang);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpPhonetic;
            _tmpPhonetic = _cursor.getString(_cursorIndexOfPhonetic);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpExampleDe;
            _tmpExampleDe = _cursor.getString(_cursorIndexOfExampleDe);
            final int _tmpBox;
            _tmpBox = _cursor.getInt(_cursorIndexOfBox);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpChapterId;
            _tmpChapterId = _cursor.getString(_cursorIndexOfChapterId);
            _item = new WordEntity(_tmpId,_tmpLang,_tmpWord,_tmpTranslation,_tmpPhonetic,_tmpPos,_tmpGender,_tmpExample,_tmpExampleDe,_tmpBox,_tmpSource,_tmpChapterId);
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
  public Object byLang(final String lang,
      final Continuation<? super List<WordEntity>> $completion) {
    final String _sql = "SELECT * FROM words WHERE lang = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lang);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WordEntity>>() {
      @Override
      @NonNull
      public List<WordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfPhonetic = CursorUtil.getColumnIndexOrThrow(_cursor, "phonetic");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfExampleDe = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleDe");
          final int _cursorIndexOfBox = CursorUtil.getColumnIndexOrThrow(_cursor, "box");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfChapterId = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterId");
          final List<WordEntity> _result = new ArrayList<WordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLang;
            _tmpLang = _cursor.getString(_cursorIndexOfLang);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpPhonetic;
            _tmpPhonetic = _cursor.getString(_cursorIndexOfPhonetic);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpExampleDe;
            _tmpExampleDe = _cursor.getString(_cursorIndexOfExampleDe);
            final int _tmpBox;
            _tmpBox = _cursor.getInt(_cursorIndexOfBox);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpChapterId;
            _tmpChapterId = _cursor.getString(_cursorIndexOfChapterId);
            _item = new WordEntity(_tmpId,_tmpLang,_tmpWord,_tmpTranslation,_tmpPhonetic,_tmpPos,_tmpGender,_tmpExample,_tmpExampleDe,_tmpBox,_tmpSource,_tmpChapterId);
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

  @Override
  public Object byChapter(final String chapterId,
      final Continuation<? super List<WordEntity>> $completion) {
    final String _sql = "SELECT * FROM words WHERE chapterId = ? ORDER BY box ASC, word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, chapterId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WordEntity>>() {
      @Override
      @NonNull
      public List<WordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLang = CursorUtil.getColumnIndexOrThrow(_cursor, "lang");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfPhonetic = CursorUtil.getColumnIndexOrThrow(_cursor, "phonetic");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfExampleDe = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleDe");
          final int _cursorIndexOfBox = CursorUtil.getColumnIndexOrThrow(_cursor, "box");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfChapterId = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterId");
          final List<WordEntity> _result = new ArrayList<WordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLang;
            _tmpLang = _cursor.getString(_cursorIndexOfLang);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpPhonetic;
            _tmpPhonetic = _cursor.getString(_cursorIndexOfPhonetic);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpExampleDe;
            _tmpExampleDe = _cursor.getString(_cursorIndexOfExampleDe);
            final int _tmpBox;
            _tmpBox = _cursor.getInt(_cursorIndexOfBox);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpChapterId;
            _tmpChapterId = _cursor.getString(_cursorIndexOfChapterId);
            _item = new WordEntity(_tmpId,_tmpLang,_tmpWord,_tmpTranslation,_tmpPhonetic,_tmpPos,_tmpGender,_tmpExample,_tmpExampleDe,_tmpBox,_tmpSource,_tmpChapterId);
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

  @Override
  public Object learnedCount(final String lang, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM words WHERE lang = ? AND box >= 2";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lang);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object totalCount(final String lang, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM words WHERE lang = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lang);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object learnedTotal(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM words WHERE box >= 2";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM words";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
