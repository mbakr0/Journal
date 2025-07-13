package online.mohmedbakr.newsfeed.data.datasources.local.database

import android.content.Context
import androidx.room.Room


/**
 * Singleton class that is used to create a reminder db
 */
object LocalDB {
    /**
     * static method that creates a reminder class and returns the DAO of the reminder
     */
    fun createArticleDao(context: Context): ArticleDao {
        return Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java, "articles.db"
        )
            .fallbackToDestructiveMigration(false)
            .build().articleDao()
    }
}