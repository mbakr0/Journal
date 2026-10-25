package online.mohmedbakr.newsfeed.data.datasources.remote.rss.extractor

interface ImageExtractor {
    fun extract(value: String): String
}
