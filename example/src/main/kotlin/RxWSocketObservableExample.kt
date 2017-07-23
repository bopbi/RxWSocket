import com.github.bobby.rxwsocket.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okio.ByteString

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

fun main(args : Array<String>) {


    val request = Request.Builder()
            .get()
            .url("ws://echo.websocket.org/") // use echo websocket test server
            .build()

    val rxWSocket = RxWSocket(OkHttpClient(), request)

    rxWSocket.webSocketObservable().subscribe {

        var webSocket : WebSocket? = null

        when (it) {
            is RxWSOpenEvent -> {
                println("Opened Observable")
                webSocket = it.webSocket
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

        if (webSocket != null) {
            rxWSocket.sendMessage(webSocket, "Ping")
        }

        // it seems echo didnt support send bytes
        // so disable it for now
        /*
        if (webSocket != null) {
            rxWSocket.sendMessageByte(webSocket, ByteString.encodeUtf8("")!!)
        }
        */

    }
}