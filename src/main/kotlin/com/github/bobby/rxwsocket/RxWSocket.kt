package com.github.bobby.rxwsocket

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
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
    fun sendMessage(webSocket: WebSocket, message: String): Single<Boolean> = Single.just(webSocket.send(message))

    fun sendMessageByte(webSocket: WebSocket, messageByte: ByteString): Single<Boolean> =
            Single.just(webSocket.send(messageByte))

    fun webSocketFlowable(mode: BackpressureStrategy): Flowable<RxWSEvent> {

        return Flowable.create({ flowable ->

            val webSocket: WebSocket? = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    flowable.onNext(RxWSEvent.OpenEvent(webSocket))
                }

                override fun onFailure(webSocket: WebSocket?, throwable: Throwable?, response: Response?) {
                    flowable.onNext(RxWSEvent.FailureEvent(webSocket, throwable, response))

                    throwable?.let {
                        flowable.onError(it)
                    }
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    flowable.onNext(RxWSEvent.ClosingEvent(webSocket, code, reason))
                    flowable.onComplete()
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    flowable.onNext(RxWSEvent.MessageStringEvent(webSocket, text))
                }

                override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                    flowable.onNext(RxWSEvent.MessageByteEvent(webSocket, bytes))
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    flowable.onNext(RxWSEvent.ClosedEvent(webSocket, code, reason))
                }
            })


            flowable.setCancellable {
                val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                val closingMessage = "Bye"
                webSocket?.close(closingCode, closingMessage)
            }

            flowable.setDisposable(object : Disposable {

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
        return Observable.create { observable ->

            val webSocket: WebSocket? = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    observable.onNext(RxWSEvent.OpenEvent(webSocket))
                }

                override fun onFailure(webSocket: WebSocket?, throwable: Throwable?, response: Response?) {
                    observable.onNext(RxWSEvent.FailureEvent(webSocket, throwable, response))
                    throwable?.let {
                        observable.onError(it)
                    }
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    observable.onNext(RxWSEvent.ClosingEvent(webSocket, code, reason))
                    observable.onComplete()
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    observable.onNext(RxWSEvent.MessageStringEvent(webSocket, text))
                }

                override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                    observable.onNext(RxWSEvent.MessageByteEvent(webSocket, bytes))
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    observable.onNext(RxWSEvent.ClosedEvent(webSocket, code, reason))
                    observable.onComplete()
                }
            })

            observable.setCancellable {

                val closingCode = 1001 // see http://tools.ietf.org/html/rfc6455#section-7.4
                val closingMessage = "Bye"
                webSocket?.close(closingCode, closingMessage)

            }

            observable.setDisposable(object : Disposable {

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
     * data class when open event
     */
    data class OpenEvent(var webSocket: WebSocket?) : RxWSEvent()

    /**
     * data class when failure event
     */
    data class FailureEvent(var webSocket: WebSocket?,
                            var throwable: Throwable?,
                            var response: Response?) : RxWSEvent()

    /**
     * data class for closing event
     */
    data class ClosingEvent(var webSocket: WebSocket?,
                            var code: Int,
                            var reason: String?) : RxWSEvent()

    /**
     * data class when string is comming
     */
    data class MessageStringEvent(var webSocket: WebSocket?,
                                  var text: String?) : RxWSEvent()

    /**
     * data class when byte is comming
     */
    data class MessageByteEvent(var webSocket: WebSocket?,
                                var bytes: ByteString?) : RxWSEvent()

    /**
     * data class when closed event
     */
    data class ClosedEvent(var webSocket: WebSocket?,
                           var code: Int,
                           var reason: String?) : RxWSEvent()
}

