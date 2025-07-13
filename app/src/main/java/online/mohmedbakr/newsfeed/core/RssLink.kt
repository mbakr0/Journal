package online.mohmedbakr.newsfeed.core

import java.net.URL

class RssLink {

    companion object {
        private const val YOUM7_BASE_URL = "https://www.youm7.com/rss/SectionRss?SectionID="
        private const val MASRAWY_BASE_URL = "https://www.masrawy.com//rss/feed/"
        private const val ALARABIYA_BASE_URL = "https://www.alarabiya.net/feed/rss2/ar"

        private const val ALHADTH_BASE_URL = "https://alhadth-online.com/rss.php?cat="
        private const val ALHADTH_ARTICLES_BASE_URL = "https://alhadth-online.com/arts_rss.php/"

        private const val RASSD_BASE_URL = "https://rassd.com/feed"

        private const val ALJAZEERA_BASE_URL =
            "https://1-a1072.azureedge.net/aljazeerarss/a7c186be-1baa-4bd4-9d80-a84db769f779/73d0e1b4-532f-45ef-b135-bfdff8b8cab9"
        private const val ARABIC_BBC_BASE_URL = "https://feeds.bbci.co.uk/arabic/rss.xml"

        private const val MADA_MASR_BASE_URL = "https://mada34.appspot.com/www.madamasr.com/ar/feed/"
    }

    fun getMasrawyLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(MASRAWY_BASE_URL + it))
        }
        return linksArray
    }

    fun getYoum7LinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(YOUM7_BASE_URL + it))
        }
        return linksArray
    }

    fun getAlarabiyaLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(ALARABIYA_BASE_URL + it))
        }
        return linksArray
    }

    fun getAlhadathLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            if (it == "0")
                linksArray.add(URL(ALHADTH_ARTICLES_BASE_URL))
            else
                linksArray.add(URL(ALHADTH_BASE_URL + it))
        }
        return linksArray
    }

    fun getRassdLink(): URL {
        return URL(RASSD_BASE_URL)
    }

    fun getAljazeeraLink(): URL {
        return URL(ALJAZEERA_BASE_URL)
    }

    fun getArabicBBCLink(): URL {
        return URL(ARABIC_BBC_BASE_URL)
    }

    fun getMadaMasr(): URL {
        return URL(MADA_MASR_BASE_URL)
    }

}