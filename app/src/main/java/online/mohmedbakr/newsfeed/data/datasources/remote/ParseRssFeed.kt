package online.mohmedbakr.newsfeed.data.datasources.remote

import android.util.Log
import online.mohmedbakr.newsfeed.core.ArticleDTO
import online.mohmedbakr.newsfeed.core.ConstantsName
import online.mohmedbakr.newsfeed.core.Response
import online.mohmedbakr.newsfeed.data.model.RssElements
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.XMLEvent

class ParseRssFeed {
    private var isTitle: Boolean = false
    private var isDescription: Boolean = false
    private var isPubdate: Boolean = false
    private var isLink: Boolean = false
    private var isItem: Boolean = false
    private var isImageUrl: Boolean = false

    private val articleList: ArrayList<ArticleDTO> = ArrayList()
    var response: Response<ArrayList<ArticleDTO>> = Response.Success(articleList)
    private val article = ArticleDTO()
    private lateinit var url: URL
    private lateinit var newspaperName: String

    private var title: String? = null
    private var link: String? = null
    private var description: String? = null
    private var publicationDate: String? = null
    private var imageLink: String? = null

    fun setNameAndUrl(url: URL, newspaperName: String) {

        this.url = url
        this.newspaperName = newspaperName
        RssElements.init(newspaperName)
        rssElements()
        streamRssFeed()
    }

    private fun rssElements() {
        title = RssElements.NEWSPAPER_TAGS[newspaperName]?.get(RssElements.TITLE)
        link = RssElements.NEWSPAPER_TAGS[newspaperName]?.get(RssElements.ARTICLE_LINK)
        description = RssElements.NEWSPAPER_TAGS[newspaperName]?.get(RssElements.DESCRIPTION)
        publicationDate = RssElements.NEWSPAPER_TAGS[newspaperName]?.get(RssElements.DATE)
        imageLink = RssElements.NEWSPAPER_TAGS[newspaperName]?.get(RssElements.IMAGE_URL)
    }

    private fun streamRssFeed() {
        val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance()
        var reader: XMLEventReader? = null
        kotlin.runCatching {
            reader = xmlInputFactory.createXMLEventReader(downloadUrl(url))
        }.onSuccess {
            response = Response.Success(articleList)
            reader?.let { it1 -> extractInfo(it1) }
        }.onFailure {
            Log.d("ViewModelNetworkState", it.message!!)
            response = Response.Error(Response.ERROR_CONNECT_TO_THE_HOST)
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(url: URL): InputStream? {
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 5000
            connectTimeout = 5000
            requestMethod = "GET"
            doInput = true
            // Starts the query.
            connect()
            inputStream
        }
    }

    private fun extractInfo(reader: XMLEventReader) {
        var event: XMLEvent
        while (reader.hasNext()) {

            event = reader.nextEvent()

            if (event.isStartElement) {
                if (event.asStartElement().name.localPart.equals("item"))
                    isItem = true

                if (!isItem)
                    continue

                startOfElement(event.asStartElement().name.localPart)
                if (event.asStartElement().name.prefix == "content" && newspaperName == RssElements.MADAMASR) isImageUrl =
                    true
                if (event.asStartElement().name.prefix == "media") isImageUrl = true

                if (isImageUrl) {
                    @Suppress("UNCHECKED_CAST")
                    val iterator: Iterator<Attribute> =
                        event.asStartElement().attributes as Iterator<Attribute>
                    this.iterateOverAttributes(iterator)
                }
            }
            if (event.isCharacters) {
                getCharactersData(event.asCharacters().data)

            }
            if (event.isEndElement) {
                endOfElement(event.asEndElement().name.toString())
            }

        }

    }

    private fun startOfElement(elementName: String?) {
        if (this.title.equals(elementName)) {
            isTitle = true
            return
        }
        if (this.link.equals(elementName)) {
            isLink = true
            return
        }
        if (this.description.equals(elementName)) {
            isDescription = true
            return
        }
        if (this.publicationDate.equals(elementName)) {
            isPubdate = true
            return
        }
        if (this.imageLink.equals(elementName)) {
            isImageUrl = true
        }
    }

    private fun iterateOverAttributes(iterator: Iterator<Attribute?>) {
        while (iterator.hasNext()) {
            val attribute = iterator.next()!!
            val name: QName = attribute.name
            if (name.toString() == "url") {
                article.imageLink = attribute.value
                isImageUrl = false
                break
            }
        }
    }

    private fun getCharactersData(data: String) {
        if (isImageUrl) {
            article.imageLink =
                if (newspaperName == RssElements.MADAMASR) extractImageLinkFromMadaMasr(data) else data
            isImageUrl = false
            return
        }

        if (isTitle) {
            article.title += data
            return
        }
        if (isLink) {
            article.link = if (newspaperName === RssElements.MADAMASR) extractArticleLink(
                data
            ) else data
            isLink = false
            return
        }
        if (isDescription) {
            article.description += extractDescription(data)
            return
        }
        if (isPubdate) {
            article.publicationDate =
                if (newspaperName == RssElements.YOUM7) translateDate(data) else data
            isPubdate = false
        }

    }

    private fun extractImageLinkFromMadaMasr(data: String): String {
        return if (data.contains("img"))
            extractArticleLink(data.split("src=\"")[1].split("\"")[0])
        else "NoImage"
    }

    private fun extractArticleLink(data: String): String {

        return data.replace("https://", "https://mada34.appspot.com/")
    }
    private fun translateDate(data: String): String {
        var translatedDate = data
        var englishDay: String
        var englishMonth: String
        var english12Hour: String

        ConstantsName.arabicMonths.forEachIndexed { index, value ->
            englishMonth = ConstantsName.englishMonths[index]
            translatedDate = translatedDate.replace(value, englishMonth)
        }

        ConstantsName.arabicDays.forEachIndexed { index, value ->
            englishDay = ConstantsName.englishDays[index]
            translatedDate = translatedDate.replace(value, englishDay)
        }

        ConstantsName.arabic_12Hour.forEachIndexed { index, value ->
            english12Hour = ConstantsName.english_12Hour[index]
            translatedDate = translatedDate.replace(" $value", " $english12Hour")
        }

        translatedDate = translatedDate.replace("،", ",")

        return translatedDate
    }

    private fun extractDescription(data: String): String {
        var str: String = data.replace("/n", "")
        str = str.replace("&quot;", "\"")

        if (RssElements.RASSD == newspaperName)
            return str.split("<p>".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].split("</p>".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]

        if (RssElements.YOUM7 == newspaperName)
            return str.split("</br>".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
        if (RssElements.MADAMASR == newspaperName)
            return str.split("<p>")[1].split("</p>")[0]

        return str
    }


    private fun endOfElement(elementName: String) {
        if (this.imageLink.equals(elementName)) {
            isImageUrl = false
            return
        }

        if (this.title.equals(elementName)) {
            isTitle = false
            return
        }

        if (this.description.equals(elementName)) {
            isDescription = false
            return
        }

        if ("item" == elementName) {
            articleList.add(article.clone(article))
            isItem = false
        }
    }

    fun clearArticleList() {
        articleList.clear()
    }
}





