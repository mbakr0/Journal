package online.mohmedbakr.newsfeed.data.datasources.remote.rss

import online.mohmedbakr.newsfeed.core.ArticleDTO
import java.io.InputStream
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

class RssXmlParser(
    private val parserConfig: ParseRssFeed,
) {
    fun parseStream(input: InputStream): List<ArticleDTO> {
        val factory = XMLInputFactory.newInstance()
        runCatching { factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false) }
        runCatching { factory.setProperty(XMLInputFactory.SUPPORT_DTD, false) }

        val parsedArticles = mutableListOf<ArticleDTO>()
        val reader = factory.createXMLEventReader(input)
        try {
            var current: ArticleDTO? = null
            var activeField: Field? = null
            val description = StringBuilder()
            val tags = parserConfig.tags

            while (reader.hasNext()) {
                val event = reader.nextEvent()
                when {
                    event.isStartElement -> {
                        val element = event.asStartElement()
                        val localName = element.name.localPart
                        if (localName.equals(TAG_ITEM, true) || localName.equals(TAG_ENTRY, true)) {
                            current = ArticleDTO()
                            activeField = null
                            description.clear()
                        } else if (current != null) {
                            val prefix = element.name.prefix.orEmpty()
                            if ((prefix == PREFIX_MEDIA || prefix == PREFIX_CONTENT) && current.imageLink.isBlank()) {
                                readUrlAttribute(element)?.let {
                                    current.imageLink = parserConfig.normalizeImage(it)
                                }
                            }

                            activeField = fieldFor(localName, tags)
                            when (activeField) {
                                Field.LINK -> readUrlAttribute(element)?.let { current.link = it }
                                Field.IMAGE -> readUrlAttribute(element)?.let {
                                    current.imageLink = parserConfig.normalizeImage(it)
                                }
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
                                current.imageLink = parserConfig.normalizeImage(value.trim())
                            }
                            null -> Unit
                        }
                    }

                    event.isEndElement -> {
                        val localName = event.asEndElement().name.localPart
                        if (current != null && localName.equals(tags.description, true)) {
                            current.description = parserConfig.extractDescription(description.toString().trim())
                        }

                        if (localName.equals(TAG_ITEM, true) || localName.equals(TAG_ENTRY, true)) {
                            current?.let { article ->
                                article.title = article.title.trim()
                                article.link = parserConfig.normalizeArticleLink(article.link.trim())
                                article.publicationDate = parserConfig.formatPublicationDate(article.publicationDate)
                                if (article.imageLink.isBlank() && description.isNotBlank()) {
                                    val extracted = parserConfig.normalizeImage(description.toString())
                                    if (extracted.isNotBlank()) {
                                        article.imageLink = extracted
                                    }
                                }
                                if (article.title.isNotBlank() && RssTextUtils.isHttpUrl(article.link)) {
                                    if (article.description.isBlank()) {
                                        article.description = parserConfig.extractDescription(description.toString().trim())
                                    }
                                    parsedArticles.add(article)
                                }
                            }
                            current = null
                            activeField = null
                            description.clear()
                        } else {
                            activeField = when {
                                localName.equals(tags.title, true) ||
                                    localName.equals(tags.link, true) ||
                                    localName.equals(tags.description, true) ||
                                    localName.equals(tags.date, true) ||
                                    localName.equals(tags.image, true) -> null
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

    private fun fieldFor(localName: String, tags: RssFeedTags): Field? = when {
        localName.equals(tags.title, true) -> Field.TITLE
        localName.equals(tags.link, true) -> Field.LINK
        localName.equals(tags.image, true) -> Field.IMAGE

        localName.equals(tags.description, true) ||
            localName.equals(TAG_SUMMARY, true) ||
            localName.equals(TAG_ENCODED, true) -> Field.DESCRIPTION

        localName.equals(tags.date, true) ||
            localName.equals(TAG_PUBLISHED, true) ||
            localName.equals(TAG_UPDATED, true) -> Field.DATE

        else -> null
    }

    private fun readUrlAttribute(element: StartElement): String? {
        @Suppress("UNCHECKED_CAST")
        val attributes = element.attributes as Iterator<javax.xml.stream.events.Attribute>
        while (attributes.hasNext()) {
            val attribute = attributes.next()
            if (attribute.name.localPart.equals(ATTR_URL, true) ||
                attribute.name.localPart.equals(ATTR_HREF, true)
            ) return attribute.value
        }
        return null
    }

    private enum class Field { TITLE, LINK, DESCRIPTION, DATE, IMAGE }

    private companion object {
        const val TAG_ITEM = "item"
        const val TAG_ENTRY = "entry"
        const val TAG_SUMMARY = "summary"
        const val TAG_ENCODED = "encoded"
        const val TAG_PUBLISHED = "published"
        const val TAG_UPDATED = "updated"
        const val ATTR_URL = "url"
        const val ATTR_HREF = "href"
        const val PREFIX_MEDIA = "media"
        const val PREFIX_CONTENT = "content"
    }
}
