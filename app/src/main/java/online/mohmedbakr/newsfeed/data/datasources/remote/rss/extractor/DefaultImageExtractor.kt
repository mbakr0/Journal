package online.mohmedbakr.newsfeed.data.datasources.remote.rss.extractor

class DefaultImageExtractor : ImageExtractor {
    override fun extract(value: String): String = value.trim()
}
