package online.mohmedbakr.newsfeed.data.datasources.remote

import android.util.Log
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.data.model.RssElements
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

/**
 * Downloads and parses one RSS feed at a time.
 *
 * The public API is kept compatible with FeedRepository. Each item is parsed into a
 * fresh DTO so malformed or incomplete items cannot leak state into the next item.
 */
class ParseRssFeed {
    private val articleList = arrayListOf<ArticleDTO>()
    var response: Response<ArrayList<ArticleDTO>> = Response.Success(articleList)
        private set

    private lateinit var newspaperName: String
    private var titleTag: String? = null
    private var linkTag: String? = null
    private var descriptionTag: String? = null
    private var dateTag: String? = null
    private var imageTag: String? = null

    fun setNameAndUrl(url: URL, newspaperName: String) {
        this.newspaperName = newspaperName
        configureTags(newspaperName)
        articleList.clear()

        try {
            val parsed = downloadAndParse(url)
            articleList.addAll(parsed.distinctBy { it.link.trim() })
            response = Response.Success(articleList)
        } catch (exception: Exception) {
            Log.w(TAG, "Unable to parse RSS feed: $url", exception)
            response = Response.Error(Response.ERROR_CONNECT_TO_THE_HOST)
        }
    }

    private fun configureTags(source: String) {
        RssElements.init(source)
        val tags = RssElements.NEWSPAPER_TAGS[source]
        titleTag = tags?.get(RssElements.TITLE)
        linkTag = tags?.get(RssElements.ARTICLE_LINK)
        descriptionTag = tags?.get(RssElements.DESCRIPTION)
        dateTag = tags?.get(RssElements.DATE)
        imageTag = tags?.get(RssElements.IMAGE_URL)
    }

    private fun downloadAndParse(url: URL): List<ArticleDTO> {
        val connection = (url.openConnection() as? HttpURLConnection)
            ?: throw IllegalArgumentException("Unsupported feed URL: $url")

        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml")
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.doInput = true

        try {
            connection.connect()
            val status = connection.responseCode
            if (status !in 200..299) {
                throw IllegalStateException("Feed server returned HTTP $status")
            }

            connection.inputStream.use { input ->
                return parseStream(input)
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseStream(input: InputStream): List<ArticleDTO> {
        val factory = XMLInputFactory.newInstance()
        setFactoryProperty(factory, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
        setFactoryProperty(factory, XMLInputFactory.SUPPORT_DTD, false)

        val parsedArticles = mutableListOf<ArticleDTO>()
        factory.createXMLEventReader(input).use { reader ->
            var current: ArticleDTO? = null
            var activeField: Field? = null
            val description = StringBuilder()

            while (reader.hasNext()) {
                val event = reader.nextEvent()
                when {
                    event.isStartElement -> {
                        val element = event.asStartElement()
                        val localName = element.name.localPart

                        if (localName.equals("item", ignoreCase = true) ||
                            localName.equals("entry", ignoreCase = true)
                        ) {
                            current = ArticleDTO()
                            activeField = null
                            description.clear()
                        } else if (current != null) {
                            val prefix = element.name.prefix.orEmpty()
                            if ((prefix == "media" || prefix == "content") && current.imageLink.isBlank()) {
                                readImageAttribute(element)?.let { current.imageLink = normalizeImage(it) }
                            }

                            activeField = fieldFor(localName)
                            if (activeField == Field.IMAGE) {
                                readImageAttribute(element)?.let { current.imageLink = normalizeImage(it) }
                            }
                        }
                    }

                    event.isCharacters && current != null -> {
                        val value = event.asCharacters().data
                        when (activeField) {
                            Field.TITLE -> current!!.title += value
                            Field.LINK -> current!!.link += value
                            Field.DESCRIPTION -> description.append(value)
                            Field.DATE -> current!!.publicationDate += value
                            Field.IMAGE -> if (current!!.imageLink.isBlank()) {
                                current!!.imageLink = normalizeImage(value.trim())
                            }
                            null -> Unit
                        }
                    }

                    event.isEndElement -> {
                        val localName = event.asEndElement().name.localPart
                        if (current != null && localName.equals(descriptionTag, ignoreCase = true)) {
                            current.description = extractDescription(description.toString().trim())
                        }

                        if (localName.equals("item", ignoreCase = true) ||
                            localName.equals("entry", ignoreCase = true)
                        ) {
                            current?.let { article ->
                                article.title = article.title.trim()
                                article.link = normalizeArticleLink(article.link.trim())
                                article.publicationDate = article.publicationDate.trim()
                                if (newspaperName == RssElements.YOUM7) {
                                    article.publicationDate = translateDate(article.publicationDate)
                                }
                                if (article.title.isNotBlank() && isHttpUrl(article.link)) {
                                    if (article.description.isBlank()) {
                                        article.description = extractDescription(description.toString().trim())
                                    }
                                    parsedArticles.add(article)
                                }
                            }
                            current = null
                            activeField = null
                            description.clear()
                        } else if (current != null) {
                            activeField = null
                        }
                    }
                }
            }
        }
        return parsedArticles
    }

    private fun fieldFor(localName: String): Field? = when {
        localName.equals(titleTag, ignoreCase = true) -> Field.TITLE
        localName.equals(linkTag, ignoreCase = true) -> Field.LINK
        localName.equals(descriptionTag, ignoreCase = true) ||
            localName.equals("summary", ignoreCase = true) ||
            localName.equals("encoded", ignoreCase = true) -> Field.DESCRIPTION
        localName.equals(dateTag, ignoreCase = true) ||
            localName.equals("published", ignoreCase = true) ||
            localName.equals("updated", ignoreCase = true) -> Field.DATE
        localName.equals(imageTag, ignoreCase = true) -> Field.IMAGE
        else -> null
    }

    private fun readImageAttribute(element: StartElement): String? {
        val attributes = element.attributes
        while (attributes.hasNext()) {
            val attribute = attributes.next()
            if (attribute.name.localPart.equals("url", ignoreCase = true) ||
                attribute.name.localPart.equals("href", ignoreCase = true)
            ) {
                return attribute.value
            }
        }
        return null
    }

    private fun normalizeImage(value: String): String {
        val candidate = if (newspaperName == RssElements.MADAMASR) {
            val match = Regex("""(?i)<img[^>]+src\\s*=\\s*["']([^"']+)["']""").find(value)
            match?.groupValues?.getOrNull(1).orEmpty()
        } else {
            value.trim()
        }
        return when {
            candidate.isBlank() -> ""
            candidate.startsWith("http://", ignoreCase = true) ||
                candidate.startsWith("https://", ignoreCase = true) -> candidate
            candidate.startsWith("//") -> "https:$candidate"
            else -> candidate
        }
    }

    private fun normalizeArticleLink(value: String): String {
        if (newspaperName == RssElements.MADAMASR) {
            return value.replace("https://", "https://mada34.appspot.com/")
        }
        return value
    }

    private fun isHttpUrl(value: String): Boolean =
        value.startsWith("https://", ignoreCase = true) ||
            value.startsWith("http://", ignoreCase = true)

    private fun extractDescription(value: String): String {
        var text = value.replace("\\n", " ").replace("/n", " ")
            .replace("&quot;", "\"")
            .trim()

        if (newspaperName == RssElements.RASSD) {
            Regex("(?is)<p\\b[^>]*>(.*?)</p>").find(text)?.groupValues?.getOrNull(1)?.let {
                text = it
            }
        } else if (newspaperName == RssElements.MADAMASR) {
            Regex("(?is)<p\\b[^>]*>(.*?)</p>").find(text)?.groupValues?.getOrNull(1)?.let {
                text = it
            }
        } else if (newspaperName == RssElements.YOUM7) {
            text = text.substringBefore("</br>", text).substringBefore("<br", text)
        }

        return text.replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
            .replace(Regex("(?s)<[^>]+>"), " ")
            .replace("&amp;", "&")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
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

    private fun setFactoryProperty(factory: XMLInputFactory, name: String, value: Any) {
        runCatching { factory.setProperty(name, value) }
    }

    fun clearArticleList() {
        articleList.clear()
        response = Response.Success(articleList)
    }

    private enum class Field { TITLE, LINK, DESCRIPTION, DATE, IMAGE }

    private companion object {
        const val TAG = "ParseRssFeed"
        const val CONNECT_TIMEOUT_MS = 8_000
        const val READ_TIMEOUT_MS = 8_000
        const val USER_AGENT = "JournalAndroid/1.0 (RSS reader)"
    }
}
