package online.mohmedbakr.newsfeed.data.datasources.remote.rss

/**
 * Encapsulates XML tag names for parsing an RSS/Atom feed source.
 */
data class RssFeedTags(
    val title: String = DEFAULT_TITLE,
    val description: String = DEFAULT_DESCRIPTION,
    val link: String = DEFAULT_LINK,
    val image: String = DEFAULT_IMAGE,
    val date: String = DEFAULT_DATE,
) {
    companion object {
        const val DEFAULT_TITLE = "title"
        const val DEFAULT_DESCRIPTION = "description"
        const val DEFAULT_LINK = "link"
        const val DEFAULT_IMAGE = "url"
        const val DEFAULT_DATE = "pubDate"
    }
}
