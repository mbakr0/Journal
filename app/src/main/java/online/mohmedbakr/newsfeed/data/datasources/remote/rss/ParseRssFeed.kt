package online.mohmedbakr.newsfeed.data.datasources.remote.rss

import android.util.Log
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.extractor.DefaultImageExtractor
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.extractor.ImageExtractor
import java.io.File
import java.net.URL

/**
 * Abstract base parser for RSS/Atom feeds.
 * Subclasses configure source-specific tags and override link, date, or description formatting.
 */
abstract class ParseRssFeed(
    val tags: RssFeedTags = RssFeedTags(),
    val imageExtractor: ImageExtractor = DefaultImageExtractor(),
) {
    protected val articleList = arrayListOf<ArticleDTO>()
    var response: Response<ArrayList<ArticleDTO>> = Response.Success(articleList)
        protected set

    open fun parse(url: URL, cacheFile: File? = null): Response<ArrayList<ArticleDTO>> {
        Log.d("LOG_SOURCE", url.toString())
        return try {
            val articles = if (cacheFile != null) {
                // Download to local cache file first
                FeedDownloader.downloadFeedToFile(url, cacheFile)
                cacheFile.inputStream().use { fileStream ->
                    RssXmlParser(this).parseStream(fileStream)
                }
            } else {
                // Direct stream parsing
                FeedDownloader.openStream(url).use { networkStream ->
                    RssXmlParser(this).parseStream(networkStream)
                }
            }

            val unique = ArrayList(articles.distinctBy { it.link.trim() })
            articleList.clear()
            articleList.addAll(unique)

            response = Response.Success(articleList)
            response
        } catch (exception: Exception) {
            Log.w(TAG, "Unable to parse RSS feed from network: $url", exception)

            // OFFLINE FALLBACK: If network/download fails, attempt to read old cached data
            if (cacheFile != null && cacheFile.exists()) {
                try {
                    val cachedArticles = cacheFile.inputStream().use { fileStream ->
                        RssXmlParser(this).parseStream(fileStream)
                    }
                    articleList.clear()
                    articleList.addAll(cachedArticles.distinctBy { it.link.trim() })

                    response = Response.Success(articleList)
                    return response
                } catch (cacheException: Exception) {
                    Log.e(TAG, "Failed to read corrupted offline cache file", cacheException)
                }
            }

            val err = Response.Error(Response.ERROR_CONNECT_TO_THE_HOST)
            response = err
            err
        }
    }

    open fun normalizeArticleLink(link: String): String = link.trim()

    open fun formatPublicationDate(date: String): String = date.trim()

    open fun normalizeImage(value: String): String {
        val candidate = imageExtractor.extract(value)
        return when {
            candidate.isBlank() -> ""
            candidate.startsWith("http://", true) || candidate.startsWith("https://", true) -> candidate
            candidate.startsWith("//") -> "https:$candidate"
            else -> candidate
        }
    }

    open fun extractDescription(value: String): String {
        val text = cleanRawDescriptionText(value)
        return cleanHtmlTags(text)
    }

    protected fun cleanRawDescriptionText(value: String): String =
        RssTextUtils.cleanRawDescription(value)

    protected fun cleanHtmlTags(text: String): String =
        RssTextUtils.cleanHtmlTags(text)

    protected fun extractParagraphText(text: String): String =
        RssTextUtils.extractParagraphText(text)

    private companion object {
        const val TAG = "ParseRssFeed"
    }
}
