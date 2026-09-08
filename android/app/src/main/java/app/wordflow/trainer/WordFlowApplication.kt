package app.wordflow.trainer

import android.app.Application

class WordFlowApplication : Application() {
    lateinit var repository: VocabRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.build(this)
        repository = VocabRepository(this, db)
    }
}
