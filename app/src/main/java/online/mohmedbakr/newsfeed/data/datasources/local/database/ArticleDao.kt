package online.mohmedbakr.newsfeed.data.datasources.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import online.mohmedbakr.newsfeed.data.model.Article

/**
 * Data Access Object for the article table.
 */

@Dao
interface ArticleDao {
    /**
     * @return all articles.
     */
    @Query("SELECT * FROM article")
    fun getArticles(): Flow<List<Article>>

    /**
     * Insert a article in the database. If the article already exists, replace it.
     *
     * @param article the article to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveArticle(article: Article)

    /**
     * Delete single article.
     */

    @Delete
    suspend fun deleteArticle(article: Article)

    /**
     * Delete all articles.
     */
    @Query("DELETE FROM article")
    suspend fun deleteAllArticles()

}