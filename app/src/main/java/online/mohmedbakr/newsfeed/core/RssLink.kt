package online.mohmedbakr.newsfeed.core

import java.net.URL

class RssLink {

    companion object {
        private const val YOUM7_BASE_URL = "https://www.youm7.com/rss/SectionRss?SectionID="
        private const val MASRAWY_BASE_URL = "https://www.masrawy.com//rss/feed/"
        private const val ALARABIYA_BASE_URL = "https://www.alarabiya.net/feed/rss2/ar"

        private const val ALHADTH_BASE_URL = "https://www.alhadath.net/.mrss/alhadath"

        private const val RASSD_BASE_URL = "https://rassd.com/feed"

        private const val ALJAZEERA_BASE_URL =
            "https://1-a1072.azureedge.net/aljazeerarss/a7c186be-1baa-4bd4-9d80-a84db769f779/73d0e1b4-532f-45ef-b135-bfdff8b8cab9"
        private const val ARABIC_BBC_BASE_URL = "https://feeds.bbci.co.uk/arabic/rss.xml"

        private const val MADA_MASR_BASE_URL = "https://www.madamasr.com/feed/"
    }

    fun getMasrawyLinksByCategories(categories: List<String>): List<URL> =
        categories.map { URL(MASRAWY_BASE_URL + it) }

    fun getYoum7LinksByCategories(categories: List<String>): List<URL> =
        categories.map { URL(YOUM7_BASE_URL + it) }

    fun getAlarabiyaLinksByCategories(categories: List<String>): List<URL> =
        categories.map { URL(ALARABIYA_BASE_URL + it) }

    fun getAlhadathLinksByCategories(categories: List<String>): List<URL> =
        categories.map { cat ->
            URL(ALHADTH_BASE_URL + cat)
        }

    fun getRassdLink(): URL = URL(RASSD_BASE_URL)

    fun getAljazeeraLink(): URL = URL(ALJAZEERA_BASE_URL)

    fun getArabicBBCLink(): URL = URL(ARABIC_BBC_BASE_URL)

    fun getMadaMasr(): URL = URL(MADA_MASR_BASE_URL)
}
