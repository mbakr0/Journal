package online.mohmedbakr.newsfeed.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.data.database.Article
import online.mohmedbakr.newsfeed.data.database.ArticleDao

class ArticleLocalRepository(
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * Get the articles list from the local db
     * @return Result the holds a Success with all the articles or an Error object with the error message
     */
    private var articles: LiveData<List<Article>> = articleDao.getArticles().asLiveData()

    fun getLiveDate(): LiveData<List<Article>> {
        return articles
    }

    /**
     * Insert a article in the db.
     * @param article the article to be inserted
     */
    suspend fun saveArticle(article: Article) =
        withContext(ioDispatcher) {
            articleDao.saveArticle(article)
        }

    /**
     * Deletes single article in the db
     */

    suspend fun deleteArticle(article: Article) =
        withContext(ioDispatcher) {
            articleDao.deleteArticle(article)
        }

    /**
     * Deletes all the articles in the db
     */
    suspend fun deleteAllArticles() =
        withContext(ioDispatcher) {
            articleDao.deleteAllArticles()
        }


}