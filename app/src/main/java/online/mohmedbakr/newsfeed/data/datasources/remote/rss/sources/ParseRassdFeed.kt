package online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources

import online.mohmedbakr.newsfeed.data.datasources.remote.rss.ParseRssFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.RssFeedTags

class ParseRassdFeed : ParseRssFeed(
    tags = RssFeedTags(link = "guid", image = "noImage")
) {
    override fun extractDescription(value: String): String {
        val raw = cleanRawDescriptionText(value)
        val paragraphText = extractParagraphText(raw)
        return cleanHtmlTags(paragraphText)
    }
}
