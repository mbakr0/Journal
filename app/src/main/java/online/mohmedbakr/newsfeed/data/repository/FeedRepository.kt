package online.mohmedbakr.newsfeed.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.core.RssLink
import online.mohmedbakr.newsfeed.data.datasources.remote.ParseRssFeed
import online.mohmedbakr.newsfeed.data.model.RssElements
import java.net.URL

/**
 * Coordinates RSS source URLs and parsing. All network/parser work runs on [ioDispatcher].
 *
 * The parser is stateful for backwards compatibility, so calls are serialized by this
 * repository instance rather than being launched concurrently.
 */
class FeedRepository(
    private val parseRssFeed: ParseRssFeed,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val rssLink = RssLink()

    suspend fun getMasrawyFeed(
        categories: ArrayList<String> = arrayListOf(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getMasrawyLinksByCategories(categories.ifEmpty { arrayListOf("25") }),
        source = RssElements.MASRAWY,
    )

    suspend fun getYoum7Feed(
        categories: ArrayList<String> = arrayListOf(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getYoum7LinksByCategories(categories.ifEmpty { arrayListOf("65") }),
        source = RssElements.YOUM7,
    )

    suspend fun getRassdFeed(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getRassdLink(), RssElements.RASSD)

    suspend fun getArabicBBCFeed(): Response<ArrayList<ArticleDTO>> =
        fetchFeed(rssLink.getArabicBBCLink(), RssElements.ARABIC_BBC)

    suspend fun getAlarabiyaFeed(
        categories: ArrayList<String> = arrayListOf(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getAlarabiyaLinksByCategories(categories.ifEmpty { arrayListOf(".xml") }),
        source = RssElements.ALARABIYA,
    )

    suspend fun getAlhadathFeed(
        categories: ArrayList<String> = arrayListOf(),
    ): Response<ArrayList<ArticleDTO>> = fetchFeeds(
        urls = rssLink.getAlhadathLinksByCategories(categories.ifEmpty { arrayListOf("3") }),
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
        parseRssFeed.clearArticleList()

        if (urls.isEmpty()) {
            return@withContext Response.Success(arrayListOf())
        }

        for (url in urls) {
            parseRssFeed.setNameAndUrl(url, source)
            when (val result = parseRssFeed.response) {
                is Response.Error -> return@withContext result
                is Response.Success -> Unit
            }
        }

        when (val result = parseRssFeed.response) {
            is Response.Success -> Response.Success(ArrayList(result.data.distinctBy { it.link.trim() }))
            is Response.Error -> result
        }
    }
}
