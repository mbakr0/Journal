package online.mohmedbakr.newsfeed.data.datasources.local.database

import android.content.Context
import androidx.room.Room

/** Provides the application's Room database as a single process-scoped instance. */
object LocalDB {
    @Volatile
    private var database: ArticleDatabase? = null

    fun getDatabase(context: Context): ArticleDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_database"
            ).build().also { database = it }
        }
    }

    fun createArticleDao(context: Context): ArticleDao = getDatabase(context).articleDao()
}
