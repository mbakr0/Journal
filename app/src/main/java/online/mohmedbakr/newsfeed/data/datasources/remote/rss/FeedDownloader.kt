package online.mohmedbakr.newsfeed.data.datasources.remote.rss

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object FeedDownloader {
    private const val CONNECT_TIMEOUT_MS = 8_000
    private const val READ_TIMEOUT_MS = 8_000
    private const val USER_AGENT_VALUE = "JournalAndroid/1.0 (RSS reader)"
    private const val HTTP_METHOD_GET = "GET"
    private const val HEADER_ACCEPT = "Accept"
    private const val HEADER_USER_AGENT = "User-Agent"
    private const val ACCEPT_HEADER_VALUE = "application/rss+xml, application/atom+xml, application/xml, text/xml"
    private const val HTTP_SUCCESS_START = 200
    private const val HTTP_SUCCESS_END = 299

    fun openStream(url: URL): InputStream {
        val connection = url.openConnection() as? HttpURLConnection
            ?: throw IllegalArgumentException("Unsupported feed URL: $url")

        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.requestMethod = HTTP_METHOD_GET
        connection.setRequestProperty(HEADER_ACCEPT, ACCEPT_HEADER_VALUE)
        connection.setRequestProperty(HEADER_USER_AGENT, USER_AGENT_VALUE)
        connection.doInput = true

        connection.connect()
        val status = connection.responseCode
        if (status !in HTTP_SUCCESS_START..HTTP_SUCCESS_END) {
            connection.disconnect()
            throw IllegalStateException("Feed server returned HTTP $status")
        }
        return connection.inputStream
    }

    /**
     * Downloads the RSS feed content directly to a local file.
     * Automatically handles network stream safety and closes the HTTP connection.
     */
    fun downloadFeedToFile(url: URL, targetFile: File) {
        openStream(url).use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
