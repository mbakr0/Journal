package online.mohmedbakr.newsfeed.data.datasources.remote.rss.extractor

class MadaMasrImageExtractor : ImageExtractor {
    override fun extract(value: String): String =
        IMG_SRC_REGEX.find(value)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            .orEmpty()

    private companion object {
        val IMG_SRC_REGEX = Regex("""(?i)<img[^>]+src\s*=\s*["']([^"']+)["']""")
    }
}
