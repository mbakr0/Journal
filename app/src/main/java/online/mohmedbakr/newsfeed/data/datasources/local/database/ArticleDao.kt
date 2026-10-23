package online.mohmedbakr.newsfeed.data.datasources.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import online.mohmedbakr.newsfeed.data.model.Article

/**
 * Data Access Object for the article table.
 */
@Dao
interface ArticleDao {
    /**
     * Returns all locally stored articles.
     */
    @Query("SELECT * FROM article")
    fun getArticles(): Flow<List<Article>>

    /**
     * Inserts an article. Existing rows with the same URL are ignored because
     * the URL is the article's stable primary key.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveArticle(article: Article)

    /**
     * Deletes a single article.
     */
    @Delete
    suspend fun deleteArticle(article: Article)

    /**
     * Deletes all locally stored articles.
     */
    @Query("DELETE FROM article")
    suspend fun deleteAllArticles()
}
