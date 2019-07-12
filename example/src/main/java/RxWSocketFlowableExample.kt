import com.github.bobby.rxwsocket.*
import io.reactivex.BackpressureStrategy
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

fun main() {

    val request = Request.Builder()
            .get()
            .url("ws://echo.websocket.org/") // use echo websocket test server
            .build()

    RxWSocket(OkHttpClient(), request)
            .webSocketFlowable(BackpressureStrategy.BUFFER)
            .subscribe {
                when (it) {
                    is RxWSEvent.OpenEvent -> {
                        println("Opened Flowable")
                        it.webSocket?.send("Send Ping")
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
    }
}