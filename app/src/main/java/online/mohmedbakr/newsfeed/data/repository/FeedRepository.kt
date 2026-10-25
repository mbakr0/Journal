package online.mohmedbakr.newsfeed.data.repository

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.core.RssLink
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.ParseRssFeedFactory
import online.mohmedbakr.newsfeed.data.model.RssElements
import java.io.File
import java.net.URL

/**
 * Coordinates RSS source URLs and parsing. All network/parser work runs on [ioDispatcher].
 */
class FeedRepository(
    private val context: Context? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val rssLink = RssLink()

    suspend fun getMasrawyFeed(
        categories: List<String> = emptyList(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getMasrawyLinksByCategories(categories.ifEmpty { listOf("25") }),
        source = RssElements.MASRAWY,
    )

    suspend fun getYoum7Feed(
        categories: List<String> = emptyList(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getYoum7LinksByCategories(categories.ifEmpty { listOf("65") }),
        source = RssElements.YOUM7,
    )

    suspend fun getRassdFeed(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getRassdLink(), RssElements.RASSD)

    suspend fun getArabicBBCFeed(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getArabicBBCLink(), RssElements.ARABIC_BBC)

    suspend fun getAlarabiyaFeed(
        categories: List<String> = emptyList(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getAlarabiyaLinksByCategories(categories.ifEmpty { listOf(".xml") }),
        source = RssElements.ALARABIYA,
    )

    suspend fun getAlhadathFeed(
        categories: List<String> = emptyList(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getAlhadathLinksByCategories(categories.ifEmpty { listOf("3") }),
        source = RssElements.ALHADATH,
    )

    suspend fun getAljazeeraFeed(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getAljazeeraLink(), RssElements.ALJAZEERA)

    suspend fun getMadaMasr(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getMadaMasr(), RssElements.MADAMASR)

    private suspend fun fetchFeed(
        url: URL,
        source: String,
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(listOf(url), source)

    private suspend fun fetchFeeds(
        urls: List<URL>,
        source: String,
    ): Response<ArrayList<ArticleDTO>> = withContext(ioDispatcher) {
        if (urls.isEmpty()) {
            return@withContext Response.Success(arrayListOf())
        }

        val parser = ParseRssFeedFactory.getParser(source)
        val combinedArticles = ArrayList<ArticleDTO>()

        for (url in urls) {
            val cacheFile = context?.cacheDir?.let { cacheDir ->
                File(cacheDir, "feed_${url.toString().hashCode()}.xml")
            }
            when (val result = parser.parse(url, cacheFile)) {
                is Response.Error -> return@withContext result
                is Response.Success -> combinedArticles.addAll(result.data)
            }
        }

        val uniqueArticles = ArrayList(combinedArticles.distinctBy { it.link.trim() })
        Response.Success(uniqueArticles)
    }
}
