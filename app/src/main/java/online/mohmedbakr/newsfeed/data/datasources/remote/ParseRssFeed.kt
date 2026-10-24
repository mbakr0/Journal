package online.mohmedbakr.newsfeed.data.datasources.remote

import android.util.Log
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.data.model.RssElements
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

/**
 * Downloads and parses RSS/Atom feeds. The API is kept compatible with FeedRepository.
 * A fresh DTO is created for every item to prevent fields leaking between articles.
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

        // Do not clear here: one repository call may combine several category feeds.
        try {
            articleList.addAll(downloadAndParse(url))
            val unique = articleList.distinctBy { it.link.trim() }
            articleList.clear()
            articleList.addAll(unique)
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
        val connection = url.openConnection() as? HttpURLConnection
            ?: throw IllegalArgumentException("Unsupported feed URL: $url")

        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.requestMethod = "GET"
        connection.setRequestProperty(
            "Accept",
            "application/rss+xml, application/atom+xml, application/xml, text/xml"
        )
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.doInput = true

        try {
            connection.connect()
            val status = connection.responseCode
            if (status !in 200..299) {
                throw IllegalStateException("Feed server returned HTTP $status")
            }
            return connection.inputStream.use(::parseStream)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseStream(input: InputStream): List<ArticleDTO> {
        val factory = XMLInputFactory.newInstance()
        setFactoryProperty(factory, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
        setFactoryProperty(factory, XMLInputFactory.SUPPORT_DTD, false)

        val parsedArticles = mutableListOf<ArticleDTO>()
        val reader = factory.createXMLEventReader(input)
        try {
            var current: ArticleDTO? = null
            var activeField: Field? = null
            val description = StringBuilder()

            while (reader.hasNext()) {
                val event = reader.nextEvent()
                when {
                    event.isStartElement -> {
                        val element = event.asStartElement()
                        val localName = element.name.localPart
                        if (localName.equals("item", true) || localName.equals("entry", true)) {
                            current = ArticleDTO()
                            activeField = null
                            description.clear()
                        } else if (current != null) {
                            val prefix = element.name.prefix.orEmpty()
                            if ((prefix == "media" || prefix == "content") && current.imageLink.isBlank()) {
                                readUrlAttribute(element)?.let { current.imageLink = normalizeImage(it) }
                            }

                            activeField = fieldFor(localName)
                            when (activeField) {
                                Field.LINK -> readUrlAttribute(element)?.let { current.link = it }
                                Field.IMAGE -> readUrlAttribute(element)?.let { current.imageLink = normalizeImage(it) }
                                else -> Unit
                            }
                        }
                    }

                    event.isCharacters && current != null -> {
                        val value = event.asCharacters().data
                        when (activeField) {
                            Field.TITLE -> current.title += value
                            Field.LINK -> current.link += value
                            Field.DESCRIPTION -> description.append(value)
                            Field.DATE -> current.publicationDate += value
                            Field.IMAGE -> if (current.imageLink.isBlank()) {
                                current.imageLink = normalizeImage(value.trim())
                            }
                            null -> Unit
                        }
                    }

                    event.isEndElement -> {
                        val localName = event.asEndElement().name.localPart
                        if (current != null && localName.equals(descriptionTag, true)) {
                            current.description = extractDescription(description.toString().trim())
                        }

                        if (localName.equals("item", true) || localName.equals("entry", true)) {
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
                        } else {
                            // Only clear a field when its own element closes. Keep collecting
                            // description text across nested HTML/XML elements.
                            activeField = when {
                                localName.equals(titleTag, true) ||
                                    localName.equals(linkTag, true) ||
                                    localName.equals(descriptionTag, true) ||
                                    localName.equals(dateTag, true) ||
                                    localName.equals(imageTag, true) -> null
                                else -> activeField
                            }
                        }
                    }
                }
            }
        } finally {
            runCatching { reader.close() }
        }
        return parsedArticles
    }

    private fun fieldFor(localName: String): Field? = when {
        localName.equals(titleTag, true) -> Field.TITLE
        localName.equals(linkTag, true) -> Field.LINK
        localName.equals(descriptionTag, true) ||
            localName.equals("summary", true) ||
            localName.equals("encoded", true) -> Field.DESCRIPTION
        localName.equals(dateTag, true) ||
            localName.equals("published", true) ||
            localName.equals("updated", true) -> Field.DATE
        localName.equals(imageTag, true) -> Field.IMAGE
        else -> null
    }

    private fun readUrlAttribute(element: StartElement): String? {
        @Suppress("UNCHECKED_CAST")
        val attributes = element.attributes as Iterator<javax.xml.stream.events.Attribute>
        while (attributes.hasNext()) {
            val attribute = attributes.next()
            if (attribute.name.localPart.equals("url", true) ||
                attribute.name.localPart.equals("href", true)
            ) return attribute.value
        }
        return null
    }

    private fun normalizeImage(value: String): String {
        val candidate = if (newspaperName == RssElements.MADAMASR) {
            Regex("""(?i)<img[^>]+src\s*=\s*["']([^"']+)["']""")
                .find(value)?.groupValues?.getOrNull(1).orEmpty()
        } else value.trim()

        return when {
            candidate.isBlank() -> ""
            candidate.startsWith("http://", true) || candidate.startsWith("https://", true) -> candidate
            candidate.startsWith("//") -> "https:$candidate"
            else -> candidate
        }
    }

    private fun normalizeArticleLink(value: String): String =
        if (newspaperName == RssElements.MADAMASR) {
            value.replace("https://", "https://mada34.appspot.com/")
        } else value

    private fun isHttpUrl(value: String): Boolean =
        value.startsWith("https://", true) || value.startsWith("http://", true)

    private fun extractDescription(value: String): String {
        var text = value.replace("\\n", " ").replace("/n", " ")
            .replace("&quot;", "\"").trim()

        if (newspaperName == RssElements.RASSD || newspaperName == RssElements.MADAMASR) {
            Regex("(?is)<p\\b[^>]*>(.*?)</p>").find(text)?.groupValues?.getOrNull(1)?.let {
                text = it
            }
        } else if (newspaperName == RssElements.YOUM7) {
            text = text.substringBefore("</br>").substringBefore("<br")
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
