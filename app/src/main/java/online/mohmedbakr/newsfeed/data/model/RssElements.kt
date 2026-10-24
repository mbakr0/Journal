package online.mohmedbakr.newsfeed.data.model

/**
 * RSS element names used by the supported publishers.
 *
 * Each publisher receives its own immutable-by-convention map. Sharing one mutable
 * map between publishers caused a later init() call to silently overwrite earlier
 * publishers' element mappings.
 */
abstract class RssElements {
    companion object {
        const val MASRAWY = "masrawy"
        const val YOUM7 = "youm7"
        const val RASSD = "rassd"
        const val ARABIC_BBC = "arabicBBC"
        const val MADAMASR = "madaMasr"
        const val ALARABIYA = "alarabiya"
        const val ALHADATH = "alhadath"
        const val ALJAZEERA = "aljazeera"

        const val TITLE = "TITLE"
        const val DESCRIPTION = "DESCRIPTION"
        const val IMAGE_URL = "URL"
        const val ARTICLE_LINK = "LINK"
        const val DATE = "DATE"

        val NEWSPAPER_TAGS = HashMap<String, Map<String, String>>()

        fun init(newspaperName: String) {
            val tags = when (newspaperName) {
                MASRAWY -> publisherTags("title", "description", "link", "url", "pubDate")
                YOUM7 -> publisherTags("title", "description", "link", "enclosure", "pubDate")
                RASSD -> publisherTags("title", "description", "guid", "noImage", "pubDate")
                ARABIC_BBC -> publisherTags("title", "description", "guid", "noImage", "pubDate")
                MADAMASR -> publisherTags("title", "description", "guid", "noImage", "pubDate")
                ALARABIYA -> publisherTags("title", "description", "link", "content", "pubDate")
                ALHADATH -> publisherTags("title", "description", "guid", "enclosure", "pubDate")
                ALJAZEERA -> publisherTags("title", "description", "guid", "noImage", "pubDate")
                else -> return
            }
            NEWSPAPER_TAGS[newspaperName] = tags
        }

        private fun publisherTags(
            title: String,
            description: String,
            link: String,
            image: String,
            date: String,
        ): Map<String, String> = mapOf(
            TITLE to title,
            DESCRIPTION to description,
            ARTICLE_LINK to link,
            IMAGE_URL to image,
            DATE to date,
        )
    }
}
