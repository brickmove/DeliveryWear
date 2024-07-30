package com.ciot.deliverywear.network

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ThreadUtils
import com.ciot.deliverywear.bean.AllStatusResponse
import com.ciot.deliverywear.bean.AllowResponse
import com.ciot.deliverywear.bean.EventBusBean
import com.ciot.deliverywear.bean.NavPointData
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.bean.RobotAllResponse
import com.ciot.deliverywear.bean.RobotData
import com.ciot.deliverywear.bean.RobotInfoResponse
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.constant.NetConstant
import com.ciot.deliverywear.network.interceptor.HttpLoggingInterceptor
import com.ciot.deliverywear.network.interceptor.TokenInterceptor
import com.ciot.deliverywear.network.tcp.RetryWithDelay
import com.ciot.deliverywear.network.tcp.TcpClient
import com.ciot.deliverywear.network.tcp.TcpMsgListener
import com.ciot.deliverywear.utils.FormatUtil
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

// 服务器网络请求管理类
class RetrofitManager {
    private val TAG = ConstantLogic.NETWORK_TAG
    private var mWuHanBaseUrl: String? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mWatchAllowDisable: Disposable? = null
    /*请求服务器超时时间*/
    private val SERVER_TIMEOUT: Long = 5000
    private var mWuhanApiService: WuhanApiService? = null
    private var mWuHanUserName: AtomicReference<String> = AtomicReference()
    private var mWuHanPassWord: AtomicReference<String> = AtomicReference()
    private var mToken: AtomicReference<String> = AtomicReference()
    private var mUserId: AtomicReference<String> = AtomicReference()
    private var mProjectId: AtomicReference<String> = AtomicReference()
    private var mProjectName: AtomicReference<String> = AtomicReference()
    private var isLoadingSuccess: AtomicReference<Boolean> = AtomicReference(false)
    private var defaultServer: AtomicReference<String> = AtomicReference()
    private var tcpIp: AtomicReference<String> = AtomicReference()

    @Volatile
    private var mNidMap: MutableMap<String, String>? = HashMap()
    @Volatile
    private var mRobotId: MutableList<String>? = mutableListOf()
    @Volatile
    private var mRobotData: MutableList<RobotData>? = mutableListOf()
    @Volatile
    private var mPoints: MutableList<String>? = mutableListOf()
    /**
     * token无效时间(单位:毫秒 Unix时间戳)
     * 到达此时间后无效
     */
    private var mTokenInvalidTime: AtomicReference<Long> = AtomicReference(0)
    //初始化状态：0表示获取到IP;1表示激活成功获取到账户和密码;2表示登录成功;3表示获取到token;4表示获取到projectId等属性信息
    private var initState = NetConstant.INIT_STATE_IDLE

    private var tcpClient: TcpClient? = null

    private object RetrofitHelperHolder {
        val holder = RetrofitManager()
    }

    companion object {
        val instance: RetrofitManager
            get() = RetrofitHelperHolder.holder
    }

    private fun getWuHanApiService(): WuhanApiService {
        if (mWuhanApiService == null) {
            val wuHanBaseUrl = getDefaultServer()
            Log.d(TAG, "getWuHanBaseUrl=$wuHanBaseUrl")
            mWuhanApiService = Retrofit.Builder()
                .baseUrl(wuHanBaseUrl)
                .client(getOkHttpClient(ConstantLogic.HTTP_TAG, true))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WuhanApiService::class.java)
            Log.d(TAG, "getWuHanApiService")
        }
        return mWuhanApiService!!
    }

    private var retryCount: Int = 0
    private var isNeedRetry: Boolean = true
    fun init() {
        Log.d(TAG, "RetrofitManager init start...")
        watchAllow(getDefaultServer(), object : Observer<ResponseBody> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
                mWatchAllowDisable = d
                addSubscription(d)
            }

            override fun onNext(response: ResponseBody) {
                val allowResponse = GsonUtils.fromJson(String(response.bytes()), AllowResponse::class.java)
                Log.d(TAG, "allowResponse: " + GsonUtils.toJson(allowResponse))
                if (allowResponse.isSuccess()) {
                    val data: AllowResponse.DataBean? = allowResponse.getData()
                    if (data != null) {
                        data.getDomain()?.let { setTcpIp(it) }
                    }
                } else {
                    Log.w(TAG, "请求接入服务器失败...")
                }
                initTcpService()
            }

            override fun onError(e: Throwable) {
                //重试10次 每次间隔三秒后才算报错。避免第一次开机无网络直接报错问题
                if (isNeedRetry && retryCount < 20) {
                    ThreadUtils.getMainHandler().postDelayed({
                        init()
                    }, 3000)
                    retryCount++
                    Log.d(TAG, "===retryCount====$retryCount")
                } else {
                    Log.e(TAG, "robotAllow error->${e.message}")
                    Log.w(TAG, "请求接入服务器失败...")
                }
            }
        })
    }

    fun initTcpService() {
        Log.d(TAG, "initTcpService...")
        tcpClient = TcpClient.getInstance(getTcpIp(), NetConstant.TCP_SERVER_PORT)
        val listener = TcpMsgListener()
        tcpClient!!.setListener(listener)
        tcpClient!!.connectAndRegister()
    }

    private fun watchAllow(baseUrl: String, observer: Observer<ResponseBody>) {
        setPropertyDomain(baseUrl)
        getWuHanApiService()
            .allow()
            .retryWhen(RetryWithDelay(40, 3000))
            .timeout(120000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /**
     * 动态切换BaseUrl
     */
    private fun setPropertyDomain(baseUrl: String) {
        val httpUrl = RetrofitUrlManager.getInstance().fetchDomain(Api.DOMAIN_NAME_PROPERTY)
        if (httpUrl == null || httpUrl.toString() != baseUrl) {
            //可以在 App 运行时随意切换某个接口的 BaseUrl
            RetrofitUrlManager.getInstance().putDomain(Api.DOMAIN_NAME_PROPERTY, baseUrl)
        }
    }

    private var getRobotsRetryCount: Int = 1
    fun getRobotsForHome(isRefreshHome: Boolean) {
        val token = getToken()
        val project = getProject()
        if (token.isNullOrEmpty() || project.isNullOrEmpty()) {
            Log.e(TAG, "getRobotsForHome param err--->token: $token, project: $project")
            return
        }
        val start ="0"
        val limit ="100"
        //Log.d(TAG, "getRobotsForHome param--->token: $token, project: $project")
        getWuHanApiService().findRobotByProject(token, project, start, limit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<RobotAllResponse>{
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(body: RobotAllResponse) {
                    Log.d(TAG, "RobotAllResponse: " + GsonUtils.toJson(body))
                    body.isRefreshHome = isRefreshHome
                    parseRobotAllResponseBody(body)
                }

                override fun onError(e: Throwable) {
                    Log.w(TAG,"getRobotsForHome onError: ${e.message}")
                    if (!isRefreshHome) {
                        if (getRobotsRetryCount <= 20) {
                            ThreadUtils.getMainHandler().postDelayed({
                                getRobotsForHome(false)
                            }, 500)
                        } else {
                            Log.e(TAG, " initWatch err count: $getRobotsRetryCount")
                        }
                        getRobotsRetryCount++
                    }
                }

                override fun onComplete() {

                }
            })
    }

    fun parseRobotAllResponseBody(body: RobotAllResponse) {
        val res: RobotAllResponse = body
        val isRefreshHome: Boolean = res.isRefreshHome
        val total: Int? = res.total
        val robotInfo: List<RobotInfoResponse>? = res.datas
        mRobotId = mutableListOf()
        mRobotData = mutableListOf()
        if (total == null || total == 0 || robotInfo.isNullOrEmpty()) {
            Log.d(TAG, "parseRobotAllResponseBody robotInfo is empty............")
            val eventBusBean = EventBusBean()
            if (isRefreshHome) {
                eventBusBean.eventType = ConstantLogic.EVENT_REFRESH_HOME
            } else {
                eventBusBean.eventType = ConstantLogic.EVENT_SHOW_HOME
            }
            EventBus.getDefault().post(eventBusBean)
            return
        }

        Observable.fromIterable(robotInfo)
            .flatMap { info ->
                val id = info.id
                val token = getToken()
                if (token.isNullOrEmpty()) {
                    return@flatMap Observable.never<Pair<RobotInfoResponse, AllStatusResponse>>()
                } else {
                    return@flatMap getWuHanApiService().getAllStatus(id, token)
                        .map { statusResponse -> Pair(info, statusResponse) }
                }
            }
            .distinct { pair -> pair.first.id }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Pair<RobotInfoResponse, AllStatusResponse>> {
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(pair: Pair<RobotInfoResponse, AllStatusResponse>) {
                    val info = pair.first
                    val status = pair.second
                    val robotData = RobotData()
                    robotData.id = info.id
                    robotData.name = info.name
                    robotData.link = info.link == true
                    robotData.label = status.getNavigation()?.getState()
                        ?.let { FormatUtil.formatLable(it) }
                    robotData.battery = status.getBattery()?.getCapacity()
                    mRobotData!!.add(robotData)
                    if (info.id.isNullOrEmpty()) {
                        mRobotId!!.add("")
                    } else {
                        mRobotId!!.add(info.id!!)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.w(TAG, "parseRobotAllResponseBody onError: ${e.message}")
                }

                override fun onComplete() {
                    Log.d(TAG, "parseRobotAllResponseBody robot list: " + GsonUtils.toJson(mRobotData))
                    val eventBusBean = EventBusBean()
                    if (isRefreshHome) {
                        eventBusBean.eventType = ConstantLogic.EVENT_REFRESH_HOME
                    } else {
                        eventBusBean.eventType = ConstantLogic.EVENT_SHOW_HOME
                    }
                    EventBus.getDefault().post(eventBusBean)
                }
            })
    }

    fun getNavPoint(robotId: String) : Observable<NavPointResponse>? {
        val token = getToken()
        if (token.isNullOrEmpty() || robotId.isEmpty()) {
            return null
        }
        return mWuhanApiService?.getNavigationPoint(token, robotId, "")
    }

    fun parsePointAllResponseBody(body: NavPointResponse) {
        val res: NavPointResponse = body
        val result: Boolean? = res.result
        val points: List<NavPointData>? = res.data
        if (result != true || points.isNullOrEmpty()) {
            return
        }
        mPoints = mutableListOf()
        points.map {
            it.getPositionName()?.let { it1 -> mPoints?.add(it1) }
        }
        Log.d(TAG, "parsePointAllResponseBody point list: " + GsonUtils.toJson(mPoints))
    }

    fun navPoint(id: String, positionName: String): Observable<ResponseBody>? {
        val token = getToken()
        if (token.isNullOrEmpty()) {
            return null
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("positionname", positionName)
//        val nid = FormatUtil.createNid()
//        mNidMap?.put(nid, positionName)
        jsonObject.addProperty("nid", setNidMap(positionName))
        Log.d(TAG, "navPoint jsonObject: " + GsonUtils.toJson(jsonObject))
        val body =  RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonObject.toString()
        )
        Log.d(TAG, "navPoint body: " + GsonUtils.toJson(body))
        return getWuHanApiService().singlePointNavigate(body, token)
    }

    @SuppressLint("All")
    fun toLogin() {
        getWuHanApiService().login(getUserRequestBody(true))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(body: ResponseBody) {
                    Log.w(TAG,"重新登录success")
                    mReLoginFailTimes = 0
                    parseLoginResponseBody(body)
                }

                override fun onError(e: Throwable) {
                    val failTimes = mReLoginFailTimes + 1
                    mReLoginFailTimes = failTimes
                    Log.w(TAG,"重新登录失败 $failTimes onError: ${e.message}")
                }

                override fun onComplete() {

                }
            })
    }

    fun firstLogin(): Observable<ResponseBody> {
        return getWuHanApiService().login(getUserRequestBody(true))
    }

    private fun getOkHttpClient(): OkHttpClient {
        return getOkHttpClient(ConstantLogic.HTTP_TAG)
    }

    private fun getOkHttpClient(tag: String, reLoginWhenTokenInvalid: Boolean = false): OkHttpClient {
        val builder = RetrofitUrlManager.getInstance().with(OkHttpClient().newBuilder())
        //设置 请求的缓存的大小跟位置
        try {
            builder.run {
                addInterceptor(HttpLoggingInterceptor(tag))
                //addInterceptor(ChuckInterceptor(ContextUtil.getContext()))
                if (reLoginWhenTokenInvalid) {
                    addInterceptor(TokenInterceptor {
                        login()
                    })
                }
                connectTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                retryOnConnectionFailure(true) // 错误重连
                proxy(Proxy.NO_PROXY) //防止被抓包，提升安全性
                val sslSocketFactory = createSSLSocketFactory()
                if (null != sslSocketFactory) {
                    sslSocketFactory(sslSocketFactory, TrustAllCerts())
                    hostnameVerifier { _, _ -> true }
                }
                // cookieJar(CookieManager())
                Log.d(TAG, "getOkHttpClient")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getOkHttpClient Exception：$e")
        }
        return builder.build()
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        var ssfFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (e: java.lang.Exception) {
        }
        return ssfFactory
    }

    //自定义SS验证相关类
    private class TrustAllCerts : X509TrustManager {

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    /**
     * 重新登录
     */
    fun reLogin() {
        val invalidTime = mTokenInvalidTime.get()
        val nowTime = System.currentTimeMillis() / 1000
        if (invalidTime > 0) {
            if (nowTime + 60 > invalidTime) {
                // 距离Token无效少于1分钟时，重新登录
                login()
            }
        }
    }

    /**
     * 重新登录失败次数
     */
    @Volatile
    private var mReLoginFailTimes = 0

    /**
     * 上一次重新登录时间
     */
    @Volatile
    private var mLastReLoginTime = 0

    /**
     * 登录
     */
    private fun login() {
        // 间隔时长
        val intervalTime = when (mReLoginFailTimes) {
            0 -> 10
            1 -> 20
            else -> 40
        }
        // 转换成秒
        val nowTime = (System.currentTimeMillis() / 1000).toInt()
        if (nowTime - mLastReLoginTime <= intervalTime) {
            Log.w(TAG,"Token过期，重新登录太频繁")
            return
        }
        toLogin()
    }

    private fun getUserRequestBody(isGetUserAndPwm: Boolean): RequestBody {
        if (initState == NetConstant.INIT_STATE_GET_IP && isGetUserAndPwm) {
            Log.d(TAG, "login condition is get")
        }
        //这里条件(账户名和密码)都满足后才开始登录
        initState = NetConstant.INIT_STATE_GET_USER
        val root = JSONObject()
        root.put("username", getWuHanUserName())
        root.put("password", getWuHanPassWord())

        val requestBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), root.toString())
        Log.d(TAG, "ready login wuhan Server, requestBody: " + GsonUtils.toJson(requestBody))
        return requestBody
    }

    fun parseLoginResponseBody(loginResponseBody: ResponseBody): Boolean {
        var token = ""
        var projectId = ""
        var projectName = ""
        //登录成功后拿到token
        try {
            val json = String(loginResponseBody.bytes())
            Log.d(TAG, "requestWuHanLogin result:$json")
            val obj = JSONObject(json).getJSONObject("data")
            token = obj.getString("token")
            Log.d(TAG, "requestWuHanLogin token:$token")
            projectId = obj.getString("projectId")
            val userId = obj.getString("user")
            setUserId(userId)
            projectName = obj.getString("name")
            // 设置token过期时长
            val timeOut = obj.getLong("timeout") * 1000
            val createTime = obj.getLong("createtime")
            setTokenInvalidTime(timeOut + createTime)
        } catch (e: Exception) {
            Log.d(TAG, "parse WuHanLogin Exception:$e")
        }
        initState = NetConstant.INIT_STATE_LONGIN_GET_TOKEN
        if (TextUtils.isEmpty(token)) {
            Log.d(TAG, "get Token is Empty")
            return false
        }
        setToken(token)
        setProject(projectId)
        setProjectName(projectName)
        Log.w(TAG, "requestWuHanLogin getToken:${getToken()}, getProject:${getProject()}, getProjectName:${getProjectName()}")
        return true
    }

    var mHandler = Handler(Looper.getMainLooper())
    /**
     * token无效时间
     */
    private fun setTokenInvalidTime(tokenInvalidTime: Long) {
        mTokenInvalidTime.set(tokenInvalidTime)
    }

    private fun setProjectName(projectName: String?) {
        mProjectName.set(projectName)
    }

    fun getProjectName(): String? {
        return mProjectName.get()
    }

    private fun setProject(projectId: String?) {
        mProjectId.set(projectId)
    }

    private fun getProject(): String? {
        return mProjectId.get()
    }

    fun getRobotList(): MutableList<String>? {
        return mRobotId
    }

    fun getRobotData(): MutableList<RobotData>? {
        return mRobotData
    }

    fun setRobotList(robotId: List<String>) {
        //设备id
        if (robotId.isNotEmpty()) {
            Log.e(TAG, "RetrofitManager setRobotId is empty")
            return
        }
        mRobotId?.addAll(robotId)
    }

    fun setWuHanUserName(userName: String?) {
        mWuHanUserName.set(userName)
    }

    fun getWuHanUserName(): String? {
        return mWuHanUserName.get()
    }

    fun setWuHanPassWord(passWord: String?) {
        mWuHanPassWord.set(passWord)
    }

    private fun getWuHanPassWord(): String? {
        return mWuHanPassWord.get()
    }

    private fun setToken(token: String) {
        this.mToken.set(token)
    }

    private fun getToken(): String? {
        return mToken.get()
    }

    private fun setUserId(userId: String) {
        this.mUserId.set(userId)
    }

    fun getUserId(): String? {
        return mUserId.get()
    }

    fun setPoints(points: MutableList<String>) {
        mPoints?.addAll(points)
    }

    fun getPoints(): MutableList<String>? {
        return mPoints
    }

    fun setIsLoading(state: Boolean) {
        this.isLoadingSuccess.set(state)
    }

    fun getPointAtIndex(index: Int): String? {
        val points = getPoints()
        if (points != null && index >= 0 && index < points.size) {
            return points[index]
        }
        return null
    }

    fun getIsLoading(): Boolean? {
        return isLoadingSuccess.get()
    }

    fun getDefaultServer(): String {
        return defaultServer.get()
    }

    fun setDefaultServer(url: String) {
        defaultServer.set(url)
    }

    private fun getTcpIp(): String {
        return tcpIp.get()
    }

    fun setTcpIp(domain: String) {
        tcpIp.set(domain)
    }

    fun getTcpClient() : TcpClient? {
        return tcpClient
    }

    fun setNidMap(value: String): String {
        val nid = FormatUtil.createNid()
        mNidMap?.put(nid, value)
        return nid
    }

    fun getPlaceName(key: String): String? {
        return mNidMap?.get(key)
    }

    fun onUnsubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    fun addSubscription(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }
}