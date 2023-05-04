package online.mohmedbakr.newsfeed.network

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

        val NEWSPAPER_TAGS = HashMap<String, HashMap<String, String>>()
        private val Tags = HashMap<String, String>()


        fun init(newspaperName: String) {
            when (newspaperName) {
                MASRAWY -> initMasrawy()
                YOUM7 -> initYoum7()
                RASSD -> initRassd()
                ARABIC_BBC -> initArabicBBC()
                MADAMASR -> initMadaMasr()
                ALARABIYA -> initAlarabiya()
                ALHADATH -> initAlhadath()
                ALJAZEERA -> initAljazeera()
            }
        }


        private fun initMasrawy() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "link"
            Tags[IMAGE_URL] = "url"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[MASRAWY] = Tags
        }


        private fun initYoum7() {

            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "link"
            Tags[IMAGE_URL] = "enclosure"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[YOUM7] = Tags
        }

        private fun initRassd() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "guid"
            Tags[IMAGE_URL] = "noImage"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[RASSD] = Tags
        }

        private fun initArabicBBC() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "guid"
            Tags[IMAGE_URL] = "noImage"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[ARABIC_BBC] = Tags
        }

        private fun initMadaMasr() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "guid"
            Tags[IMAGE_URL] = "noImage"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[MADAMASR] = Tags
        }

        private fun initAlarabiya() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "link"
            Tags[IMAGE_URL] = "content"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[ALARABIYA] = Tags
        }

        private fun initAlhadath() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "guid"
            Tags[IMAGE_URL] = "enclosure"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[ALHADATH] = Tags
        }

        private fun initAljazeera() {
            Tags[TITLE] = "title"
            Tags[DESCRIPTION] = "description"
            Tags[ARTICLE_LINK] = "guid"
            Tags[IMAGE_URL] = "noImage"
            Tags[DATE] = "pubDate"
            NEWSPAPER_TAGS[ALJAZEERA] = Tags
        }
    }
}