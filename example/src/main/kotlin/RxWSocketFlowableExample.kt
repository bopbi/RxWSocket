import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

fun main(args : Array<String>) {

    val request = Request.Builder()
            .get()
            .url("ws://echo.websocket.org/") // use echo websocket test server
            .build()

    RxWSocket(OkHttpClient(), request).webSocketFlowable().subscribe {

        when (it) {
            is RxWSOpenEvent -> {
                println("Opened Flowable")
                it?.webSocket?.send("Send Ping")
            }

            is RxWSMessageStringEvent -> {
                println("Receive Message String: " + it.text)
            }

            is RxWSMessageByteEvent -> {
                println("Receive Message Byte: " + it.bytes)
            }

            is RxWSClosingEvent -> {
                println("Closing")
            }

            is RxWSFailureEvent -> {
                println("Failure")
            }

            is RxWSClosedEvent -> {
                println("Closed")
            }

        }
    }
}