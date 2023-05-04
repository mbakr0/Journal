package online.mohmedbakr.newsfeed.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Article::class], version = 2, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

}