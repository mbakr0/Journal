package online.mohmedbakr.newsfeed.data.datasources.remote.rss

import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseAlarabiyaFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseAlhadathFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseAljazeeraFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseArabicBbcFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseMadaMasrFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseMasrawyFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseRassdFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources.ParseYoum7Feed
import online.mohmedbakr.newsfeed.data.model.RssElements

object ParseRssFeedFactory {
    fun getParser(source: String): ParseRssFeed = when (source) {
        RssElements.MASRAWY -> ParseMasrawyFeed()
        RssElements.YOUM7 -> ParseYoum7Feed()
        RssElements.RASSD -> ParseRassdFeed()
        RssElements.ARABIC_BBC -> ParseArabicBbcFeed()
        RssElements.MADAMASR -> ParseMadaMasrFeed()
        RssElements.ALARABIYA -> ParseAlarabiyaFeed()
        RssElements.ALHADATH -> ParseAlhadathFeed()
        RssElements.ALJAZEERA -> ParseAljazeeraFeed()
        else -> throw IllegalArgumentException("Unknown news source: $source")
    }
}
