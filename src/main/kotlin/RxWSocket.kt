import io.reactivex.*
import io.reactivex.Observable.create
import okhttp3.*
import okio.ByteString


/**
 * Created by bobbyadiprabowo on 7/14/17.
 */

/**
 * RxWebSocket class
 * @constructor
 */
class RxWSocket(val client:OkHttpClient, val request: Request) {

    /**
     *
     */
    fun sendMessage(webSocket: WebSocket, message: String) {
        webSocket.send(message)
    }

    fun webSocketFlowable(): Flowable<RxWSEvent> {
        return Flowable.create({

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                it.onNext(RxWSOpenEvent(webSocket))
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                it.onNext(RxWSFailureEvent(webSocket,t,response))
                it.onError(t)
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                it.onNext(RxWSClosingEvent(webSocket, code, reason))
                it.onComplete()
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                it.onNext(RxWSMessageStringEvent(webSocket, text))
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                it.onNext(RxWSMessageByteEvent(webSocket, bytes))
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                it.onNext(RxWSClosedEvent(webSocket, code, reason))
                it.onComplete()
            }
        })

        }, BackpressureStrategy.BUFFER)

    }

    fun webSocketObservable(): Observable<RxWSEvent> {
        return create{


            client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    it.onNext(RxWSOpenEvent(webSocket))
                }

                override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                    it.onNext(RxWSFailureEvent(webSocket,t,response))
                    it.onError(t)
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSClosingEvent(webSocket, code, reason))
                    it.onComplete()
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    it.onNext(RxWSMessageStringEvent(webSocket, text))
                }

                override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                    it.onNext(RxWSMessageByteEvent(webSocket, bytes))
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSClosedEvent(webSocket, code, reason))
                    it.onComplete()
                }
            })

        }
    }

}

/**
 *
 */
sealed class RxWSEvent

/**
 *
 */
data class RxWSOpenEvent(var webSocket: WebSocket?) : RxWSEvent()

/**
 *
 */
data class RxWSFailureEvent(var webSocket: WebSocket?, var throwable: Throwable?, var response: Response?) : RxWSEvent()

/**
 *
 */
data class RxWSClosingEvent(var webSocket: WebSocket?, var code: Int, var reason: String?) : RxWSEvent()

/**
 * data class when
 */
data class RxWSMessageStringEvent(var webSocket: WebSocket?, var text: String?) : RxWSEvent()

/**
 *
 */
data class RxWSMessageByteEvent(var webSocket: WebSocket?, var bytes: ByteString?) : RxWSEvent()

/**
 *
 */
data class RxWSClosedEvent(var webSocket: WebSocket?, var code: Int, var reason: String?) : RxWSEvent()