package online.mohmedbakr.newsfeed.data.datasources.local.database

import android.content.Context
import androidx.room.Room

/**
 * Provides the application's local article database.
 *
 * Room databases are expensive to create, so keep a single instance for
 * the lifetime of the application process.
 */
object LocalDB {
    @Volatile
    private var database: ArticleDatabase? = null

    fun createArticleDao(context: Context): ArticleDao {
        return getDatabase(context).articleDao()
    }

    private fun getDatabase(context: Context): ArticleDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "articles.db"
            )
                .fallbackToDestructiveMigration(false)
                .build()
                .also { database = it }
        }
    }
}
