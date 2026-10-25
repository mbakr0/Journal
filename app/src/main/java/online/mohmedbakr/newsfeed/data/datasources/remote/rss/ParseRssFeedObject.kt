package online.mohmedbakr.newsfeed.data.datasources.remote.rss

object ParseRssFeedObject {
    fun getParser(source: String): ParseRssFeed = ParseRssFeedFactory.getParser(source)
}
