package app.wordflow.trainer;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WordDao _wordDao;

  private volatile ChapterDao _chapterDao;

  private volatile ActivityDao _activityDao;

  private volatile DailyDao _dailyDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `words` (`id` TEXT NOT NULL, `lang` TEXT NOT NULL, `word` TEXT NOT NULL, `translation` TEXT NOT NULL, `phonetic` TEXT NOT NULL, `pos` TEXT NOT NULL, `gender` TEXT NOT NULL, `example` TEXT NOT NULL, `exampleDe` TEXT NOT NULL, `box` INTEGER NOT NULL, `source` TEXT NOT NULL, `chapterId` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chapters` (`id` TEXT NOT NULL, `lang` TEXT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `activity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `langId` TEXT NOT NULL, `title` TEXT NOT NULL, `words` INTEGER NOT NULL, `accuracy` INTEGER NOT NULL, `date` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily` (`date` TEXT NOT NULL, `count` INTEGER NOT NULL, PRIMARY KEY(`date`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bb133118c67f8283b4e9122629a2232b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `words`");
        db.execSQL("DROP TABLE IF EXISTS `chapters`");
        db.execSQL("DROP TABLE IF EXISTS `activity`");
        db.execSQL("DROP TABLE IF EXISTS `daily`");
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
        final HashMap<String, TableInfo.Column> _columnsWords = new HashMap<String, TableInfo.Column>(12);
        _columnsWords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("lang", new TableInfo.Column("lang", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("word", new TableInfo.Column("word", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("translation", new TableInfo.Column("translation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("phonetic", new TableInfo.Column("phonetic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("pos", new TableInfo.Column("pos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("example", new TableInfo.Column("example", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("exampleDe", new TableInfo.Column("exampleDe", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("box", new TableInfo.Column("box", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("chapterId", new TableInfo.Column("chapterId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWords = new TableInfo("words", _columnsWords, _foreignKeysWords, _indicesWords);
        final TableInfo _existingWords = TableInfo.read(db, "words");
        if (!_infoWords.equals(_existingWords)) {
          return new RoomOpenHelper.ValidationResult(false, "words(app.wordflow.trainer.WordEntity).\n"
                  + " Expected:\n" + _infoWords + "\n"
                  + " Found:\n" + _existingWords);
        }
        final HashMap<String, TableInfo.Column> _columnsChapters = new HashMap<String, TableInfo.Column>(4);
        _columnsChapters.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("lang", new TableInfo.Column("lang", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChapters.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChapters = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChapters = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChapters = new TableInfo("chapters", _columnsChapters, _foreignKeysChapters, _indicesChapters);
        final TableInfo _existingChapters = TableInfo.read(db, "chapters");
        if (!_infoChapters.equals(_existingChapters)) {
          return new RoomOpenHelper.ValidationResult(false, "chapters(app.wordflow.trainer.ChapterEntity).\n"
                  + " Expected:\n" + _infoChapters + "\n"
                  + " Found:\n" + _existingChapters);
        }
        final HashMap<String, TableInfo.Column> _columnsActivity = new HashMap<String, TableInfo.Column>(7);
        _columnsActivity.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("langId", new TableInfo.Column("langId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("words", new TableInfo.Column("words", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("accuracy", new TableInfo.Column("accuracy", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActivity = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActivity = new TableInfo("activity", _columnsActivity, _foreignKeysActivity, _indicesActivity);
        final TableInfo _existingActivity = TableInfo.read(db, "activity");
        if (!_infoActivity.equals(_existingActivity)) {
          return new RoomOpenHelper.ValidationResult(false, "activity(app.wordflow.trainer.ActivityEntity).\n"
                  + " Expected:\n" + _infoActivity + "\n"
                  + " Found:\n" + _existingActivity);
        }
        final HashMap<String, TableInfo.Column> _columnsDaily = new HashMap<String, TableInfo.Column>(2);
        _columnsDaily.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDaily.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDaily = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDaily = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDaily = new TableInfo("daily", _columnsDaily, _foreignKeysDaily, _indicesDaily);
        final TableInfo _existingDaily = TableInfo.read(db, "daily");
        if (!_infoDaily.equals(_existingDaily)) {
          return new RoomOpenHelper.ValidationResult(false, "daily(app.wordflow.trainer.DailyEntity).\n"
                  + " Expected:\n" + _infoDaily + "\n"
                  + " Found:\n" + _existingDaily);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "bb133118c67f8283b4e9122629a2232b", "278d2231471cf1ce11679e42c4ac80ac");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "words","chapters","activity","daily");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `words`");
      _db.execSQL("DELETE FROM `chapters`");
      _db.execSQL("DELETE FROM `activity`");
      _db.execSQL("DELETE FROM `daily`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(WordDao.class, WordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChapterDao.class, ChapterDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ActivityDao.class, ActivityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyDao.class, DailyDao_Impl.getRequiredConverters());
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
  public WordDao words() {
    if (_wordDao != null) {
      return _wordDao;
    } else {
      synchronized(this) {
        if(_wordDao == null) {
          _wordDao = new WordDao_Impl(this);
        }
        return _wordDao;
      }
    }
  }

  @Override
  public ChapterDao chapters() {
    if (_chapterDao != null) {
      return _chapterDao;
    } else {
      synchronized(this) {
        if(_chapterDao == null) {
          _chapterDao = new ChapterDao_Impl(this);
        }
        return _chapterDao;
      }
    }
  }

  @Override
  public ActivityDao activity() {
    if (_activityDao != null) {
      return _activityDao;
    } else {
      synchronized(this) {
        if(_activityDao == null) {
          _activityDao = new ActivityDao_Impl(this);
        }
        return _activityDao;
      }
    }
  }

  @Override
  public DailyDao daily() {
    if (_dailyDao != null) {
      return _dailyDao;
    } else {
      synchronized(this) {
        if(_dailyDao == null) {
          _dailyDao = new DailyDao_Impl(this);
        }
        return _dailyDao;
      }
    }
  }
}
