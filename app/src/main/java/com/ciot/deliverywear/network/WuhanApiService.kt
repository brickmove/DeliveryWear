package com.ciot.deliverywear.network
import io.reactivex.Observable
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface WuhanApiService {
    /*登录*/
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + Api.DOMAIN_NAME_PROPERTY)
    @POST("/api/Users/codeLogin")
    fun login(@Body body: RequestBody): Observable<ResponseBody>

}
