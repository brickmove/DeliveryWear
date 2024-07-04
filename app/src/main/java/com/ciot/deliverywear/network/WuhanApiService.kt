package com.ciot.deliverywear.network
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.bean.RobotAllResponse
import io.reactivex.Observable
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface WuhanApiService {
    /*登录*/
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + Api.DOMAIN_NAME_PROPERTY)
    @POST("/api/Users/codeLogin")
    fun login(@Body body: RequestBody): Observable<ResponseBody>

    /*获取导航点*/
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + Api.DOMAIN_NAME_PROPERTY)
    @GET("/api/Robots/ctrl/getNavigate")
    fun getNavigationPoint(@Query("access_token") token: String?,
                           @Query("id") robot: String?,
                           @Query("map") map: String?)
    : Observable<NavPointResponse>

    /*单点导航*/
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + Api.DOMAIN_NAME_PROPERTY)
    @POST("/api/Robots/ctrl/singlePointNavigate")
    fun singlePointNavigate(@Body body: RequestBody?, @Query("access_token") token: String?): Observable<ResponseBody>

    /*根据项目id获取机器人列表*/
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + Api.DOMAIN_NAME_PROPERTY)
    @GET("/api/Robots/findByProject")
    fun findRobotByProject(@Query("access_token") token: String?,
                           @Query("projectid") projectId: String?,
                           @Query("start") start: String?,
                           @Query("limit") limit: String?,
    ): Observable<RobotAllResponse>


}
