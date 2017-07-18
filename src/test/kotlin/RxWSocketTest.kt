import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import okhttp3.*
import okio.ByteString
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

class RxWSocketTest {

    lateinit var mockRxWSocket: RxWSocket
    lateinit var mockWebSocket: WebSocket

    @Before
    fun setup() {
        var mockOkhttp : OkHttpClient = mock(OkHttpClient::class.java)
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()

        mockRxWSocket = RxWSocket(mockOkhttp, request)

        mockWebSocket = mock(WebSocket::class.java)
        `when`(mockWebSocket.send("")).thenReturn(true)
    }

    @Test
    fun testFlowableSubscribed() {
        TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            assertSubscribed()
        }
    }

    @Test
    fun testFlowableOpenEvent() {

        val mockRxOpenEvent = RxWSOpenEvent(mock(WebSocket::class.java))
        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)


            onNext(mockRxOpenEvent)

            assertValue(mockRxOpenEvent)
        }

        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRxOpenEvent)
    }

    @Test
    fun testFlowableFailureEvent() {
        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()

        val mockRxFailureEvent = RxWSFailureEvent(mock(WebSocket::class.java)
                , mock(Throwable::class.java)
                , Response.Builder()
                .request(request)
                .code(200)
                .message("")
                .protocol(Protocol.HTTP_1_0)
                .build())

        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRxFailureEvent)

            assertValue(mockRxFailureEvent)
        }

        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRxFailureEvent)
    }

    @Test
    fun testFlowableClosingEvent() {
        val mockRxClosingEvent = RxWSClosingEvent(mock(WebSocket::class.java), 200, "")

        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRxClosingEvent)
            assertValue(mockRxClosingEvent)
        }
        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRxClosingEvent)
    }

    @Test
    fun testFlowableMessageStringEvent() {
        val mockRXMessageStringEvent = RxWSMessageStringEvent(mock(WebSocket::class.java), "")

        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)
            onNext(mockRXMessageStringEvent)
            assertValue(mockRXMessageStringEvent)
        }
        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRXMessageStringEvent)
    }

    @Test
    fun testFlowableMessageByteEvent() {
        val mockRXMessageByteEvent = RxWSMessageByteEvent(mock(WebSocket::class.java), mock(ByteString::class.java))
        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRXMessageByteEvent)
            assertValue(mockRXMessageByteEvent)
        }
        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRXMessageByteEvent)
    }

    @Test
    fun testFlowableClosedEvent() {
        val mockRxClosedEvent = RxWSClosedEvent(mock(WebSocket::class.java), 200, "")

        val subscriber = TestSubscriber<RxWSEvent>().apply {
            mockRxWSocket.webSocketFlowable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRxClosedEvent)
            assertValue(mockRxClosedEvent)
        }
        assertThat(subscriber.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(subscriber.values().first(), mockRxClosedEvent)
    }

    @Test
    fun testObservableSubscribed() {
        TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            assertSubscribed()
        }
    }

    @Test
    fun testObservableOpenEvent() {

        val mockRxOpenEvent = RxWSOpenEvent(mock(WebSocket::class.java))
        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)


            onNext(mockRxOpenEvent)

            assertValue(mockRxOpenEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRxOpenEvent)
    }

    @Test
    fun testObservableFailureEvent() {

        val request = Request.Builder()
                .get()
                .url("ws://192.168.0.1:8080/ws")
                .build()
        val mockRxFailureEvent = RxWSFailureEvent(mock(WebSocket::class.java)
                , mock(Throwable::class.java)
                , Response.Builder()
                .request(request)
                .code(200)
                .message("")
                .protocol(Protocol.HTTP_1_0)
                .build())

        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)


            onNext(mockRxFailureEvent)

            assertValue(mockRxFailureEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRxFailureEvent)
    }

    @Test
    fun testObservableClosingEvent() {
        val mockRxClosingEvent = RxWSClosingEvent(mock(WebSocket::class.java), 200, "")
        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRxClosingEvent)
            assertValue(mockRxClosingEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRxClosingEvent)
    }

    @Test
    fun testObservableMessageStringEvent() {
        val mockRXMessageStringEvent = RxWSMessageStringEvent(mock(WebSocket::class.java), "")
        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRXMessageStringEvent)
            assertValue(mockRXMessageStringEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRXMessageStringEvent)
    }

    @Test
    fun testObservableMessageByteEvent() {
        val mockRXMessageByteEvent = RxWSMessageByteEvent(mock(WebSocket::class.java), mock(ByteString::class.java))
        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRXMessageByteEvent)
            assertValue(mockRXMessageByteEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRXMessageByteEvent)
    }

    @Test
    fun testObservableClosedEvent() {
        val mockRxClosedEvent = RxWSClosedEvent(mock(WebSocket::class.java), 200, "")
        val observer = TestObserver<RxWSEvent>().apply {
            mockRxWSocket.webSocketObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribe(this)

            onNext(mockRxClosedEvent)
            assertValue(mockRxClosedEvent)
        }
        assertThat(observer.values().first(), instanceOf(RxWSEvent::class.java))
        assertEquals(observer.values().first(), mockRxClosedEvent)
    }

    @Test
    fun testSendMessage() {
        val observer = mockRxWSocket.sendMessage(mockWebSocket, "")
                    .test()

        observer.assertComplete()
    }

    @Test
    fun testSendMessageBytes() {
        val observer = mockRxWSocket.sendMessageByte(mockWebSocket, ByteString.EMPTY)
                .test()

        observer.assertComplete()
    }
}