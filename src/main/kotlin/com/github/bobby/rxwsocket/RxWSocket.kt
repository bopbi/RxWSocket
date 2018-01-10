package com.github.bobby.rxwsocket

import io.reactivex.*
import io.reactivex.Observable.create
import io.reactivex.disposables.Disposable
import okhttp3.*
import okio.ByteString


/**
 * Created by bobbyadiprabowo on 7/14/17.
 */

/**
 * RxWebSocket class
 * @constructor
 */
class RxWSocket(private val client: OkHttpClient, private val request: Request) {

    /**
     *
     */
    fun sendMessage (webSocket: WebSocket, message: String) : Single<Boolean> = Single.just(webSocket.send(message))

    fun sendMessageByte (webSocket: WebSocket, messageByte: ByteString) : Single<Boolean> =
            Single.just(webSocket.send(messageByte))

    fun webSocketFlowable(mode: BackpressureStrategy): Flowable<RxWSEvent> {

        return Flowable.create({

            val webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    it.onNext(RxWSEvent.OpenEvent(webSocket))
                }

                override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                    it.onNext(RxWSEvent.FailureEvent(webSocket,t,response))
                    if (t != null) {
                        it.onError(t)
                    }
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSEvent.ClosingEvent(webSocket, code, reason))
                    it.onComplete()
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    it.onNext(RxWSEvent.MessageStringEvent(webSocket, text))
                }

                override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                    it.onNext(RxWSEvent.MessageByteEvent(webSocket, bytes))
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSEvent.ClosedEvent(webSocket, code, reason))
                }
            })


            it.setCancellable({

                val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                val closingMessage = "Bye"
                webSocket?.close(closingCode, closingMessage)

            })

            it.setDisposable(object : Disposable {

                var disposed = false

                override fun isDisposed(): Boolean {
                    return disposed
                }

                override fun dispose() {
                    val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                    val closingMessage = "Bye"
                    if (webSocket != null) {
                        disposed = webSocket.close(closingCode, closingMessage)
                    }
                }

            })
        }, mode)

    }

    fun webSocketObservable(): Observable<RxWSEvent> {
        return create{

            val webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    it.onNext(RxWSEvent.OpenEvent(webSocket))
                }

                override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                    it.onNext(RxWSEvent.FailureEvent(webSocket,t,response))
                    if (t != null) {
                        it.onError(t)
                    }
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSEvent.ClosingEvent(webSocket, code, reason))
                    it.onComplete()
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    it.onNext(RxWSEvent.MessageStringEvent(webSocket, text))
                }

                override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                    it.onNext(RxWSEvent.MessageByteEvent(webSocket, bytes))
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    it.onNext(RxWSEvent.ClosedEvent(webSocket, code, reason))
                    it.onComplete()
                }
            })

            it.setCancellable({

                val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                val closingMessage = "Bye"
                webSocket?.close(closingCode, closingMessage)

            })

            it.setDisposable(object : Disposable {

                var disposed = false

                override fun isDisposed(): Boolean = disposed

                override fun dispose() {
                    val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                    val closingMessage = "Bye"
                    if (webSocket != null) {
                        disposed = webSocket.close(closingCode, closingMessage)
                    }
                }

            })

        }
    }

}

/**
 *
 */
sealed class RxWSEvent {
    /**
     *
     */
    data class OpenEvent(var webSocket: WebSocket?) : RxWSEvent()

    /**
     *
     */
    data class FailureEvent(var webSocket: WebSocket?, var throwable: Throwable?, var response: Response?) : RxWSEvent()

    /**
     *
     */
    data class ClosingEvent(var webSocket: WebSocket?, var code: Int, var reason: String?) : RxWSEvent()

    /**
     * data class when
     */
    data class MessageStringEvent(var webSocket: WebSocket?, var text: String?) : RxWSEvent()

    /**
     *
     */
    data class MessageByteEvent(var webSocket: WebSocket?, var bytes: ByteString?) : RxWSEvent()

    /**
     *
     */
    data class ClosedEvent(var webSocket: WebSocket?, var code: Int, var reason: String?) : RxWSEvent()
}

