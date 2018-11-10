import com.github.bobby.rxwsocket.*
import com.nhaarman.mockito_kotlin.mock
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
        val mockOkhttp : OkHttpClient = mock()
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

        val mockRxOpenEvent = RxWSEvent.OpenEvent(mock())
        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxOpenEvent)

        testSubscriber.assertValueAt(0) { it is RxWSEvent.OpenEvent}
        testSubscriber.assertValueAt(0) {(it as RxWSEvent.OpenEvent).equals(mockRxOpenEvent)}

    }

    @Test
    fun testFlowableFailureEvent() {
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()

        val errorCode = 500
        val mockRxFailureEvent = RxWSEvent.FailureEvent(mock()
                , mock()
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

        testSubscriber.assertValueAt(0) { it is RxWSEvent.FailureEvent}
        testSubscriber.assertValueAt(0) { (it as RxWSEvent.FailureEvent).response!!.code() == errorCode}
    }

    @Test
    fun testFlowableClosingEvent() {
        val closeCode = 200
        val mockRxClosingEvent = RxWSEvent.ClosingEvent(mock(), 200, "")

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxClosingEvent)

        testSubscriber.assertValueAt(0) { it is RxWSEvent.ClosingEvent}
        testSubscriber.assertValueAt(0) { (it as RxWSEvent.ClosingEvent).code == closeCode}
    }

    @Test
    fun testFlowableMessageStringEvent() {
        val mockString = "test"
        val mockRXMessageStringEvent = RxWSEvent.MessageStringEvent(mock(), mockString)

        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRXMessageStringEvent)

        testSubscriber.assertValueAt(0) { it is RxWSEvent.MessageStringEvent }
        testSubscriber.assertValueAt(0) { (it as RxWSEvent.MessageStringEvent).text.equals(mockString) }

    }

    @Test
    fun testFlowableMessageByteEvent() {
        val byteStringMock = mock(ByteString::class.java)
        val mockRXMessageByteEvent = RxWSEvent.MessageByteEvent(mock(), byteStringMock)
        val testSubscriber = mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRXMessageByteEvent)

        testSubscriber.assertValueAt(0) { it is RxWSEvent.MessageByteEvent }
        testSubscriber.assertValueAt(0) { (it as RxWSEvent.MessageByteEvent).bytes == byteStringMock }
    }

    @Test
    fun testFlowableClosedEvent() {
        val closeCode = 200
        val mockRxClosedEvent = RxWSEvent.ClosedEvent(mock(), closeCode, "")

        val testSubscriber =  mockRxWSocket.webSocketFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testSubscriber.onNext(mockRxClosedEvent)

        testSubscriber.assertValueAt(0) { it is RxWSEvent.ClosedEvent}
        testSubscriber.assertValueAt(0) { (it as RxWSEvent.ClosedEvent).code == closeCode}
    }

    @Test
    fun testFlowableClosedEventByCancel() {
        val mockRxClosedEvent = RxWSEvent.ClosedEvent(mock(), 1001, "Bye")

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

        val mockRxOpenEvent = RxWSEvent.OpenEvent(mock())
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()


        testObserver.onNext(mockRxOpenEvent)

        testObserver.assertValueAt(0) { it is RxWSEvent.OpenEvent}
    }

    @Test
    fun testObservableFailureEvent() {

        val errorCode = 500
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()
        val mockRxFailureEvent = RxWSEvent.FailureEvent(mock()
                , mock()
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

        testObserver.assertValueAt(0) { it is RxWSEvent.FailureEvent}
        testObserver.assertValueAt(0) { (it as RxWSEvent.FailureEvent).response!!.code() == errorCode}
    }

    @Test
    fun testObservableClosingEvent() {
        val closeCode = 200
        val mockRxClosingEvent = RxWSEvent.ClosingEvent(mock(), closeCode, "")
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRxClosingEvent)

        testObserver.assertValueAt(0) { it is RxWSEvent.ClosingEvent}
        testObserver.assertValueAt(0) { (it as RxWSEvent.ClosingEvent).code == closeCode}
    }

    @Test
    fun testObservableMessageStringEvent() {
        val mockString = "test"
        val mockRXMessageStringEvent = RxWSEvent.MessageStringEvent(mock(), mockString)
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRXMessageStringEvent)

        testObserver.assertValueAt(0) { it is RxWSEvent.MessageStringEvent}
        testObserver.assertValueAt(0) { (it as RxWSEvent.MessageStringEvent).text.equals(mockString) }
    }

    @Test
    fun testObservableMessageByteEvent() {
        val mockByteString = mock(ByteString::class.java)
        val mockRXMessageByteEvent = RxWSEvent.MessageByteEvent(mock(), mockByteString)
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRXMessageByteEvent)

        testObserver.assertValueAt(0) { it is RxWSEvent.MessageByteEvent}
        testObserver.assertValueAt(0) { (it as RxWSEvent.MessageByteEvent).bytes == mockByteString }
    }

    @Test
    fun testObservableClosedEvent() {
        val closeCode = 200
        val mockRxClosedEvent = RxWSEvent.ClosedEvent(mock(), closeCode, "")
        val testObserver = mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.trampoline())
                    .observeOn(Schedulers.trampoline())
                    .test()

        testObserver.onNext(mockRxClosedEvent)

        testObserver.assertValueAt(0) { it is RxWSEvent.ClosedEvent}
        testObserver.assertValueAt(0) { (it as RxWSEvent.ClosedEvent).code == closeCode}
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
        mockWebSocket = mock()
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
        mockWebSocket = mock()
        whenever(mockWebSocket.send(ByteString.EMPTY)).thenReturn(false)
        val observer = mockRxWSocket.sendMessageByte(mockWebSocket, ByteString.EMPTY)
                .test()

        observer.assertComplete()
        observer.assertValue(false)
    }
}