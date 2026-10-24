package online.mohmedbakr.newsfeed.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.data.datasources.local.database.ArticleDao
import online.mohmedbakr.newsfeed.data.model.Article

/**
 * Repository for locally cached and bookmarked articles.
 *
 * Room owns the observable query; write operations are dispatched to IO so callers
 * can safely invoke them from a ViewModel coroutine.
 */
class ArticleLocalRepository(
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val articles: LiveData<List<Article>> = articleDao.getArticles().asLiveData()

    fun getLiveData(): LiveData<List<Article>> = articles

    /**
     * Kept for source compatibility with existing callers. Prefer [getLiveData].
     */
    @Deprecated("Use getLiveData()")
    fun getLiveDate(): LiveData<List<Article>> = getLiveData()

    suspend fun saveArticle(article: Article) = withContext(ioDispatcher) {
        articleDao.saveArticle(article)
    }

    suspend fun deleteArticle(article: Article) = withContext(ioDispatcher) {
        articleDao.deleteArticle(article)
    }

    suspend fun deleteAllArticles() = withContext(ioDispatcher) {
        articleDao.deleteAllArticles()
    }
}
