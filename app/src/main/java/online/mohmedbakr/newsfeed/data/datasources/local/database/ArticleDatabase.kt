package online.mohmedbakr.newsfeed.data.datasources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import online.mohmedbakr.newsfeed.data.model.Article

@Database(entities = [Article::class], version = 2, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

}