package online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources

import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.ParseRssFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.RssFeedTags

class ParseYoum7Feed : ParseRssFeed(
    tags = RssFeedTags(image = "enclosure")
) {
    override fun formatPublicationDate(date: String): String = translateDate(date)

    override fun extractDescription(value: String): String {
        val raw = cleanRawDescriptionText(value)
            .substringBefore("</br>")
            .substringBefore("<br")
        return cleanHtmlTags(raw)
    }

    private fun translateDate(data: String): String {
        var translated = data
        ConstantsName.arabicMonths.forEachIndexed { index, value ->
            ConstantsName.englishMonths.getOrNull(index)?.let { translated = translated.replace(value, it) }
        }
        ConstantsName.arabicDays.forEachIndexed { index, value ->
            ConstantsName.englishDays.getOrNull(index)?.let { translated = translated.replace(value, it) }
        }
        ConstantsName.arabic_12Hour.forEachIndexed { index, value ->
            ConstantsName.english_12Hour.getOrNull(index)?.let {
                translated = translated.replace(" $value", " $it")
            }
        }
        return translated.replace("،", ",")
    }

}
