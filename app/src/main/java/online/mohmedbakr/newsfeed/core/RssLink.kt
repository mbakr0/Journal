package online.mohmedbakr.newsfeed.core

import java.net.URL

class RssLink {

    companion object {
        private const val youm7Baseurl = "https://www.youm7.com/rss/SectionRss?SectionID="
        private const val masrawyBaseurl = "https://www.masrawy.com//rss/feed/"
        private const val alarabiyaBaseUrl = "https://www.alarabiya.net/feed/rss2/ar"

        private const val alhadathBaseUrl = "https://alhadth-online.com/rss.php?cat="
        private const val alhadathArticlesURL = "https://alhadth-online.com/arts_rss.php/"

        private const val rassdBaseUrl = "https://rassd.com/feed"

        private const val aljazeeraBaseurl =
            "https://1-a1072.azureedge.net/aljazeerarss/a7c186be-1baa-4bd4-9d80-a84db769f779/73d0e1b4-532f-45ef-b135-bfdff8b8cab9"
        private const val arabicBBCBaseurl = "https://feeds.bbci.co.uk/arabic/rss.xml"

        private const val madaMasr = "https://mada34.appspot.com/www.madamasr.com/ar/feed/"
    }

    fun getMasrawyLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(masrawyBaseurl + it))
        }
        return linksArray
    }

    fun getYoum7LinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(youm7Baseurl + it))
        }
        return linksArray
    }

    fun getAlarabiyaLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            linksArray.add(URL(alarabiyaBaseUrl + it))
        }
        return linksArray
    }

    fun getAlhadathLinksByCategories(categories: ArrayList<String>): ArrayList<URL> {
        val linksArray = ArrayList<URL>()
        categories.forEach {
            if (it == "0")
                linksArray.add(URL(alhadathArticlesURL))
            else
                linksArray.add(URL(alhadathBaseUrl + it))
        }
        return linksArray
    }

    fun getRassdLink(): URL {
        return URL(rassdBaseUrl)
    }

    fun getAljazeeraLink(): URL {
        return URL(aljazeeraBaseurl)
    }

    fun getArabicBBCLink(): URL {
        return URL(arabicBBCBaseurl)
    }

    fun getMadaMasr(): URL {
        return URL(madaMasr)
    }

}