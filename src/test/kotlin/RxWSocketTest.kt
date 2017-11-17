import com.github.bobby.rxwsocket.*
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.BackpressureStrategy
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okio.ByteString
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

class RxWSocketTest {

    lateinit var mockRxWSocket: RxWSocket
    lateinit var mockWebSocket: WebSocket

    @Before
    fun setup() {
        val mockOkhttp : OkHttpClient = mock(OkHttpClient::class.java)
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()

        mockRxWSocket = RxWSocket(mockOkhttp, request)

        mockWebSocket = mock(WebSocket::class.java)
        whenever(mockWebSocket.send("")).thenReturn(true)
    }

    @Test
    fun testFlowableSubscribed() {

        mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .test()
                .assertSubscribed()
    }

    @Test
    fun testFlowableOpenEvent() {

        val mockRxOpenEvent = RxWSOpenEvent(mock(WebSocket::class.java))
        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxOpenEvent)

        testSubscriber.assertValueAt(0, { it is RxWSOpenEvent})
        testSubscriber.assertValueAt(0,  {(it as RxWSOpenEvent).equals(mockRxOpenEvent)})

    }

    @Test
    fun testFlowableFailureEvent() {
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()

        val errorCode = 500
        val mockRxFailureEvent = RxWSFailureEvent(mock(WebSocket::class.java)
                , mock(Throwable::class.java)
                , Response.Builder()
                .request(request)
                .code(errorCode)
                .message("")
                .protocol(Protocol.HTTP_1_0)
                .build())

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxFailureEvent)

        testSubscriber.assertValueAt(0, { it is RxWSFailureEvent})
        testSubscriber.assertValueAt(0,  { (it as RxWSFailureEvent).response!!.code() == errorCode})
    }

    @Test
    fun testFlowableClosingEvent() {
        val closeCode = 200
        val mockRxClosingEvent = RxWSClosingEvent(mock(WebSocket::class.java), 200, "")

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxClosingEvent)

        testSubscriber.assertValueAt(0, { it is RxWSClosingEvent})
        testSubscriber.assertValueAt(0,  { (it as RxWSClosingEvent).code == closeCode})
    }

    @Test
    fun testFlowableMessageStringEvent() {
        val mockString = "test"
        val mockRXMessageStringEvent = RxWSMessageStringEvent(mock(WebSocket::class.java), mockString)

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRXMessageStringEvent)

        testSubscriber.assertValueAt(0, { it is RxWSMessageStringEvent })
        testSubscriber.assertValueAt(0, { (it as RxWSMessageStringEvent).text.equals(mockString) })

    }

    @Test
    fun testFlowableMessageByteEvent() {
        val byteStringMock = mock(ByteString::class.java)
        val mockRXMessageByteEvent = RxWSMessageByteEvent(mock(WebSocket::class.java), byteStringMock)
        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRXMessageByteEvent)

        testSubscriber.assertValueAt(0, { it is RxWSMessageByteEvent })
        testSubscriber.assertValueAt(0, { (it as RxWSMessageByteEvent).bytes == byteStringMock })
    }

    @Test
    fun testFlowableClosedEvent() {
        val closeCode = 200
        val mockRxClosedEvent = RxWSClosedEvent(mock(WebSocket::class.java), closeCode, "")

        val testSubscriber =  mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxClosedEvent)

        testSubscriber.assertValueAt(0, { it is RxWSClosedEvent})
        testSubscriber.assertValueAt(0,  { (it as RxWSClosedEvent).code == closeCode})
    }

    @Test
    fun testFlowableClosedEventByCancel() {
        val mockRxClosedEvent = RxWSClosedEvent(mock(WebSocket::class.java), 1001, "Bye")

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxClosedEvent)

        testSubscriber.cancel()

        testSubscriber.assertNotComplete()
    }

    @Test
    fun testObservableSubscribed() {
        mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test().assertSubscribed()
    }

    @Test
    fun testObservableOpenEvent() {

        val mockRxOpenEvent = RxWSOpenEvent(mock(WebSocket::class.java))
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()


        testObserver.onNext(mockRxOpenEvent)

        testObserver.assertValueAt(0, { it is RxWSOpenEvent})
    }

    @Test
    fun testObservableFailureEvent() {

        val errorCode = 500
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()
        val mockRxFailureEvent = RxWSFailureEvent(mock(WebSocket::class.java)
                , mock(Throwable::class.java)
                , Response.Builder()
                .request(request)
                .code(errorCode)
                .message("")
                .protocol(Protocol.HTTP_1_0)
                .build())

        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()


        testObserver.onNext(mockRxFailureEvent)

        testObserver.assertValueAt(0, { it is RxWSFailureEvent})
        testObserver.assertValueAt(0,  { (it as RxWSFailureEvent).response!!.code() == errorCode})
    }

    @Test
    fun testObservableClosingEvent() {
        val closeCode = 200
        val mockRxClosingEvent = RxWSClosingEvent(mock(WebSocket::class.java), closeCode, "")
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRxClosingEvent)

        testObserver.assertValueAt(0, { it is RxWSClosingEvent})
        testObserver.assertValueAt(0, { (it as RxWSClosingEvent).code == closeCode})
    }

    @Test
    fun testObservableMessageStringEvent() {
        val mockString = "test"
        val mockRXMessageStringEvent = RxWSMessageStringEvent(mock(WebSocket::class.java), mockString)
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRXMessageStringEvent)

        testObserver.assertValueAt(0, { it is RxWSMessageStringEvent})
        testObserver.assertValueAt(0, { (it as RxWSMessageStringEvent).text.equals(mockString) })
    }

    @Test
    fun testObservableMessageByteEvent() {
        val mockByteString = mock(ByteString::class.java)
        val mockRXMessageByteEvent = RxWSMessageByteEvent(mock(WebSocket::class.java), mockByteString)
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRXMessageByteEvent)

        testObserver.assertValueAt(0, { it is RxWSMessageByteEvent})
        testObserver.assertValueAt(0, { (it as RxWSMessageByteEvent).bytes == mockByteString })
    }

    @Test
    fun testObservableClosedEvent() {
        val closeCode = 200
        val mockRxClosedEvent = RxWSClosedEvent(mock(WebSocket::class.java), closeCode, "")
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRxClosedEvent)

        testObserver.assertValueAt(0, { it is RxWSClosedEvent})
        testObserver.assertValueAt(0, { (it as RxWSClosedEvent).code == closeCode})
    }

    @Test
    fun testSendMessage() {
        val observer = mockRxWSocket.sendMessage(mockWebSocket, "")
                    .test()

        observer.assertComplete()
        observer.assertResult(true)
    }

    @Test
    fun testFailedSendMessage() {
        mockWebSocket = mock(WebSocket::class.java)
        whenever(mockWebSocket.send("")).thenReturn(false)

        val observer = mockRxWSocket.sendMessage(mockWebSocket, "")
                .test()

        observer.assertComplete()
        observer.assertResult(false)
    }

    @Test
    fun testSendMessageBytes() {
        whenever(mockWebSocket.send(ByteString.EMPTY)).thenReturn(true)
        val observer = mockRxWSocket.sendMessageByte(mockWebSocket, ByteString.EMPTY)
                .test()

        observer.assertComplete()
        observer.assertValue(true)
    }

    @Test
    fun testFailedSendMessageBytes() {
        mockWebSocket = mock(WebSocket::class.java)
        whenever(mockWebSocket.send(ByteString.EMPTY)).thenReturn(false)
        val observer = mockRxWSocket.sendMessageByte(mockWebSocket, ByteString.EMPTY)
                .test()

        observer.assertComplete()
        observer.assertValue(false)
    }
}