package online.mohmedbakr.newsfeed.data.datasources.remote.rss

object RssTextUtils {
    private val SCRIPT_TAG_REGEX = Regex("(?is)<script[^>]*>.*?</script>")
    private val STYLE_TAG_REGEX = Regex("(?is)<style[^>]*>.*?</style>")
    private val HTML_TAG_REGEX = Regex("(?s)<[^>]+>")
    private val WHITESPACE_REGEX = Regex("\\s+")
    private val PARAGRAPH_TAG_REGEX = Regex("(?is)<p\\b[^>]*>(.*?)</p>")

    fun cleanRawDescription(value: String): String =
        value
            //.replace("\\n", " ")
          //  .replace("/n", " ")
           // .replace("&quot;", "\"")
            .trim()

    fun cleanHtmlTags(text: String): String =
        text
           // .replace(SCRIPT_TAG_REGEX, " ")
       //     .replace(STYLE_TAG_REGEX, " ")
         //   .replace(HTML_TAG_REGEX, " ")
        //  .replace("&amp;", "&")
        //   .replace("&nbsp;", " ")
      //      .replace(WHITESPACE_REGEX, " ")
            .trim()

    fun extractParagraphText(text: String): String =
        PARAGRAPH_TAG_REGEX.find(text)?.groupValues?.getOrNull(1) ?: text

    fun isHttpUrl(value: String): Boolean =
        value.startsWith("https://", true) || value.startsWith("http://", true)
}
