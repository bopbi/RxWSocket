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
        if (it is RxWSOpenEvent) {
            println("Opened")
            it.webSocket?.send("Send")
        }

        if (it is RxWSMessageStringEvent) {
            println("Message String: " + it.text)
        }

        if (it is RxWSMessageByteEvent) {
            print("Message Byte: " + it.bytes)
        }

        if (it is RxWSClosingEvent) {
            print("Closing")
        }

        if (it is RxWSFailureEvent) {
            print("Failure")
        }

        if (it is RxWSClosedEvent) {
            print("Closed")
        }
    }
}