import okhttp3.Request

/**
 * Created by bobbyadiprabowo on 7/15/17.
 */

fun main(args : Array<String>) {

    val request = Request.Builder()
            .get()
            .url("ws://10.10.0.2:8080/ws")
            .build()

    RxWSocket()
}