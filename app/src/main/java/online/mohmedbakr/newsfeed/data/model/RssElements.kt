package online.mohmedbakr.newsfeed.data.model

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

        // A separate map per publisher prevents init() from overwriting other sources.
        val NEWSPAPER_TAGS = HashMap<String, HashMap<String, String>>()

        fun init(newspaperName: String) {
            val tags = when (newspaperName) {
                MASRAWY -> tags("title", "description", "link", "url", "pubDate")
                YOUM7 -> tags("title", "description", "link", "enclosure", "pubDate")
                RASSD, ARABIC_BBC, MADAMASR, ALJAZEERA ->
                    tags("title", "description", "guid", "noImage", "pubDate")
                ALARABIYA -> tags("title", "description", "link", "content", "pubDate")
                ALHADATH -> tags("title", "description", "guid", "enclosure", "pubDate")
                else -> return
            }
            NEWSPAPER_TAGS[newspaperName] = tags
        }

        private fun tags(
            title: String,
            description: String,
            link: String,
            image: String,
            date: String,
        ) = hashMapOf(
            TITLE to title,
            DESCRIPTION to description,
            ARTICLE_LINK to link,
            IMAGE_URL to image,
            DATE to date,
        )
    }
}
