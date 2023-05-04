package online.mohmedbakr.newsfeed.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.core.RssLink
import online.mohmedbakr.newsfeed.network.ParseRssFeed
import online.mohmedbakr.newsfeed.network.RssElements
import java.net.URL

class FeedRepository(
    private val parseRssFeed: ParseRssFeed,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val rssLink = RssLink()
    private var newspaperName: String = ""
    private lateinit var url: URL
    private lateinit var urlList: ArrayList<URL>

    suspend fun getMasrawyFeed(categories: ArrayList<String> = arrayListOf()): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            if (categories.isEmpty())
                categories.add("25")
            urlList = rssLink.getMasrawyLinksByCategories(categories)
            newspaperName = RssElements.MASRAWY
            urlList.forEach {
                parseRssFeed.setNameAndUrl(it, newspaperName)
                if (parseRssFeed.response is Response.Error)
                    return@withContext parseRssFeed.response

            }
            return@withContext parseRssFeed.response
        }

    }

    suspend fun getYoum7Feed(
        categories: ArrayList<String> = arrayListOf(),
    ): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            if (categories.isEmpty())
                categories.add("65")
            urlList = rssLink.getYoum7LinksByCategories(categories)
            newspaperName = RssElements.YOUM7

            urlList.forEach {
                parseRssFeed.setNameAndUrl(it, newspaperName)
                if (parseRssFeed.response is Response.Error)
                    return@withContext parseRssFeed.response
            }
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getRassdFeed(): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            url = rssLink.getRassdLink()
            newspaperName = RssElements.RASSD
            parseRssFeed.setNameAndUrl(url, newspaperName)
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getArabicBBCFeed(): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            url = rssLink.getArabicBBCLink()
            newspaperName = RssElements.ARABIC_BBC
            parseRssFeed.setNameAndUrl(url, newspaperName)
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getAlarabiyaFeed(categories: ArrayList<String> = arrayListOf()): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            if (categories.isEmpty())
                categories.add(".xml")
            urlList = rssLink.getAlarabiyaLinksByCategories(categories)
            newspaperName = RssElements.ALARABIYA
            urlList.forEach { url: URL ->
                parseRssFeed.setNameAndUrl(url, newspaperName)
                if (parseRssFeed.response is Response.Error)
                    return@withContext parseRssFeed.response
            }
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getAlhadathFeed(categories: ArrayList<String> = arrayListOf()): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            if (categories.isEmpty())
                categories.add("3")
            urlList = rssLink.getAlhadathLinksByCategories(categories)
            newspaperName = RssElements.ALHADATH
            urlList.forEach {
                parseRssFeed.setNameAndUrl(it, newspaperName)
                if (parseRssFeed.response is Response.Error)
                    return@withContext parseRssFeed.response
            }
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getAljazeeraFeed(): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            url = rssLink.getAljazeeraLink()
            newspaperName = RssElements.ALJAZEERA
            parseRssFeed.setNameAndUrl(url, newspaperName)
            return@withContext parseRssFeed.response
        }
    }

    suspend fun getMadaMasr(): Response<ArrayList<ArticleDTO>> {
        return withContext(ioDispatcher) {
            parseRssFeed.clearArticleList()
            url = rssLink.getMadaMasr()
            newspaperName = RssElements.MADAMASR
            parseRssFeed.setNameAndUrl(url, newspaperName)
            return@withContext parseRssFeed.response
        }
    }

}