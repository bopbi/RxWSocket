import com.github.bobby.rxwsocket.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

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
            is RxWSEvent.OpenEvent -> {
                println("Opened Observable")
                webSocket = it.webSocket
            }

            is RxWSEvent.MessageStringEvent -> {
                println("Receive Message String: " + it.text)
            }

            is RxWSEvent.MessageByteEvent -> {
                println("Receive Message Byte: " + it.bytes)
            }

            is RxWSEvent.ClosingEvent -> {
                println("Closing")
            }

            is RxWSEvent.FailureEvent -> {
                println("Failure")
            }

            is RxWSEvent.ClosedEvent -> {
                println("Closed")
            }

        }

        if (webSocket != null) {
            rxWSocket.sendMessage(webSocket, "Ping")
            webSocket.close(1001, "Bye")
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