package app.wordflow.trainer

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE lang = :lang ORDER BY box ASC, word ASC")
    fun observeByLang(lang: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE lang = :lang")
    suspend fun byLang(lang: String): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words WHERE lang = :lang AND box >= 2")
    suspend fun learnedCount(lang: String): Int

    @Query("SELECT COUNT(*) FROM words WHERE lang = :lang")
    suspend fun totalCount(lang: String): Int

    @Query("SELECT COUNT(*) FROM words WHERE box >= 2")
    suspend fun learnedTotal(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("UPDATE words SET box = :box WHERE id = :id")
    suspend fun updateBox(id: String, box: Int)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity ORDER BY createdAt DESC LIMIT 20")
    fun observe(): Flow<List<ActivityEntity>>

    @Insert
    suspend fun insert(item: ActivityEntity)
}

@Dao
interface DailyDao {
    @Query("SELECT * FROM daily")
    fun observe(): Flow<List<DailyEntity>>

    @Query("SELECT * FROM daily WHERE date = :date")
    suspend fun get(date: String): DailyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DailyEntity)
}

@Database(entities = [WordEntity::class, ActivityEntity::class, DailyEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun words(): WordDao
    abstract fun activity(): ActivityDao
    abstract fun daily(): DailyDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "wordflow.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
