package online.mohmedbakr.newsfeed.core


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */

sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val errorType: Int) : Response<Nothing>()

    companion object ErrorType {
        const val NO_INTERNET_CONNECTION = 1000
        const val NO_NEWSPAPER_SELECTED = 1001
        const val ERROR_CONNECT_TO_THE_HOST = 1002

        const val ERROR_CONNECT_TO_THE_HOST_MESSAGE = "Can't Connect To the web Site"
    }
}
