package com.ciot.deliverywear.network.tcp
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RetryWithDelay : Function<Observable<out Throwable>, Observable<*>> {

    private var maxRetryCount = 3 // 可重试次数
    private var retryDelayMillis: Long = 3000 // 重试等待时间
    private var isFilterHttp = false //是否过滤HTTP请求异常  非接口请求场景需要设置为ture

    constructor()

    constructor(retryDelayMillis: Long) {
        this.retryDelayMillis = retryDelayMillis
    }

    constructor(maxRetryCount: Int, retryDelayMillis: Long) {
        this.maxRetryCount = maxRetryCount
        this.retryDelayMillis = retryDelayMillis
    }

    constructor(maxRetryCount: Int, retryDelayMillis: Long, isFilterHttp: Boolean) {
        this.maxRetryCount = maxRetryCount
        this.retryDelayMillis = retryDelayMillis
        this.isFilterHttp = isFilterHttp
    }

    @Throws(Exception::class)
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable
                .zipWith(Observable.range(1, maxRetryCount + 1),
                        BiFunction<Throwable, Int, Wrapper> { t1, t2 -> Wrapper(t2, t1) })
                .flatMap { wrapper ->
                    val t = wrapper.throwable
                    if ((t is ConnectException
                                    || t is SocketTimeoutException
                                    || t is TimeoutException
                                    || t is HttpException)
                            && wrapper.index < maxRetryCount + 1) {
                        if (isFilterHttp) {
                            Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                        } else {
                            Observable.timer(retryDelayMillis * wrapper.index, TimeUnit.MILLISECONDS)
                        }
                    } else Observable.error<Any>(wrapper.throwable)
                }
    }

    private inner class Wrapper(val index: Int, val throwable: Throwable)

}
