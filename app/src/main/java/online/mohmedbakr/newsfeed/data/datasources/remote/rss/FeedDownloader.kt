//package online.mohmedbakr.newsfeed.data.datasources.remote.rss
//
//import java.io.File
//import java.io.InputStream
//import java.net.HttpURLConnection
//import java.net.URL
//
//object FeedDownloader {
//    private const val CONNECT_TIMEOUT_MS = 10_000
//    private const val READ_TIMEOUT_MS = 10_000
//    private const val USER_AGENT_VALUE = "JournalAndroid/1.0 (RSS reader)"
//    private const val HTTP_METHOD_GET = "GET"
//    private const val HEADER_ACCEPT = "Accept"
//    private const val HEADER_USER_AGENT = "User-Agent"
//    private const val ACCEPT_HEADER_VALUE = "application/rss+xml, application/atom+xml, application/xml, text/xml"
//    private const val HTTP_SUCCESS_START = 200
//    private const val HTTP_SUCCESS_END = 299
//
//    fun openStream(url: URL): InputStream {
//        val connection = url.openConnection() as? HttpURLConnection
//            ?: throw IllegalArgumentException("Unsupported feed URL: $url")
//
//        connection.connectTimeout = CONNECT_TIMEOUT_MS
//        connection.readTimeout = READ_TIMEOUT_MS
//        connection.requestMethod = HTTP_METHOD_GET
//        connection.setRequestProperty(HEADER_ACCEPT, ACCEPT_HEADER_VALUE)
//        connection.setRequestProperty(HEADER_USER_AGENT, USER_AGENT_VALUE)
//        connection.doInput = true
//
//        connection.connect()
//        val status = connection.responseCode
//        if (status !in HTTP_SUCCESS_START..HTTP_SUCCESS_END) {
//            connection.disconnect()
//            throw IllegalStateException("Feed server returned HTTP $status")
//        }
//        return connection.inputStream
//    }
//
//    /**
//     * Downloads the RSS feed content directly to a local file.
//     * Automatically handles network stream safety and closes the HTTP connection.
//     */
//    fun downloadFeedToFile(url: URL, targetFile: File) {
//        openStream(url).use { inputStream ->
//            targetFile.outputStream().use { outputStream ->
//                inputStream.copyTo(outputStream)
//            }
//        }
//    }
//}

package online.mohmedbakr.newsfeed.data.datasources.remote.rss

import android.util.Log
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.net.Inet4Address
import java.net.UnknownHostException
import java.net.URL
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionSpec
import okhttp3.Protocol
import okhttp3.TlsVersion

object FeedDownloader {

    private const val CONNECT_TIMEOUT_MS = 8_000L
    private const val READ_TIMEOUT_MS = 8_000L

    private const val USER_AGENT_VALUE = "JournalAndroid/1.0 (RSS reader)"
    private const val ACCEPT_HEADER_VALUE =
        "application/rss+xml, application/atom+xml, application/xml, text/xml"

    private val ipv4OnlyHosts = setOf(
        "madamasr.com",
        "www.madamasr.com",
        "rassd.com",
        "www.rassd.com"
    )

    private val client = OkHttpClient.Builder()
        .dns { hostname ->
            val addresses = Dns.SYSTEM.lookup(hostname)

            Log.d(
                "FeedDNS",
                "$hostname resolved: ${
                    addresses.joinToString {
                        "${it.hostAddress} (${it.javaClass.simpleName})"
                    }
                }"
            )

            if (hostname.equals("rassd.com", ignoreCase = true)) {
                val ipv4 = addresses.filterIsInstance<Inet4Address>()

                Log.d("FeedDNS", "Rassd IPv4 candidates: $ipv4")

                if (ipv4.isEmpty()) {
                    throw UnknownHostException(
                        "No IPv4 address found for $hostname"
                    )
                }

                ipv4
            } else {
                addresses
            }
        }
        .connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .build()

    fun openStream(url: URL): InputStream {
        val request = Request.Builder()
            .url(url)
            .header("Accept", ACCEPT_HEADER_VALUE)
            .header("User-Agent", USER_AGENT_VALUE)
            .get()
            .build()

        val requestClient =
            if (url.host.equals("rassd.com", ignoreCase = true)) {
                client.newBuilder()
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .connectionSpecs(
                        listOf(
                            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                                .tlsVersions(TlsVersion.TLS_1_2)
                                .build()
                        )
                    )
                    .build()
            } else {
                client
            }

        val response = requestClient.newCall(request).execute()

        if (!response.isSuccessful) {
            val status = response.code
            response.close()
            throw IllegalStateException(
                "Feed server returned HTTP $status"
            )
        }

        return response.body.byteStream()
    }

    fun downloadFeedToFile(url: URL, targetFile: File) {
        openStream(url).use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}