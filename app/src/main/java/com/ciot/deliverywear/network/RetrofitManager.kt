package com.ciot.deliverywear.network

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.ciot.deliverywear.bean.NavPointData
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.bean.RobotAllResponse
import com.ciot.deliverywear.bean.RobotInfoResponse
import com.ciot.deliverywear.constant.HttpConstant
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.NotNull

// 服务器网络请求管理类
class RetrofitManager {
    private val TAG = "NETWORK_TAG"
    private var mWuHanBaseUrl: String? = null
    private var mCompositeDisposable: CompositeDisposable? = null

    /*请求服务器超时时间*/
    private val SERVER_TIMEOUT: Long = 5000
    private var mWuhanApiService: WuhanApiService? = null
    private var mWuHanUserName: AtomicReference<String> = AtomicReference()
    private var mWuHanPassWord: AtomicReference<String> = AtomicReference()
    private var mToken: AtomicReference<String> = AtomicReference()
    private var mUserId: AtomicReference<String> = AtomicReference()
    private var mProjectId: AtomicReference<String> = AtomicReference()
    @Volatile
    private var mRobotId: MutableList<String>? = mutableListOf()
    /**
     * token无效时间(单位:毫秒 Unix时间戳)
     * 到达此时间后无效
     */
    private var mTokenInvalidTime: AtomicReference<Long> = AtomicReference(0)
    //初始化状态：0表示获取到IP;1表示激活成功获取到账户和密码;2表示登录成功;3表示获取到token;4表示获取到projectId等属性信息
    var initState = HttpConstant.INIT_STATE_IDLE

    private object RetrofitHelperHolder {
        val holder = RetrofitManager()
    }

    companion object {
        val instance: RetrofitManager
            get() = RetrofitHelperHolder.holder
    }

    private fun getWuHanApiService(): WuhanApiService {
        if (mWuhanApiService == null) {
            val wuHanBaseUrl = HttpConstant.DEFAULT_SERVICE_URL
            Log.d(TAG, "getWuHanBaseUrl=$wuHanBaseUrl")
            mWuhanApiService = Retrofit.Builder()
                .baseUrl(wuHanBaseUrl)
                .client(getOkHttpClient(TAG, true))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WuhanApiService::class.java)
            Log.d(TAG, "getWuHanApiService")
        }
        return mWuhanApiService!!
    }

    fun getRobots() {
        val token = getToken()
        val project = getProject()
        if (token.isNullOrEmpty() || project.isNullOrEmpty()) {
            Log.e(TAG, "param err--->token: $token, project: $project")
            return
        }
        val start ="0"
        val limit ="100"
        getWuHanApiService().findRobotByProject(token, project, start, limit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<RobotAllResponse>{
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(body: RobotAllResponse) {
                    Log.d(TAG, "RobotAllResponse: " + GsonUtils.toJson(body))
                    parseRobotAllResponseBody(body)
                }

                override fun onError(e: Throwable) {
                    Log.w(TAG,"onError: ${e.message}")
                }

                override fun onComplete() {
                }
            })
    }

    private fun parseRobotAllResponseBody(body: RobotAllResponse) {
        val res: RobotAllResponse = body
        val total: Int? = res.total
        val robotInfo: List<RobotInfoResponse>? = res.datas
        if (total == null || total == 0 || robotInfo.isNullOrEmpty()) {
            return
        }
        mRobotId = mutableListOf()
        robotInfo.map {
            it.id?.let { it1 -> mRobotId?.add(it1) }
        }
        Log.d(TAG, "parseRobotAllResponseBody robot list: " + GsonUtils.toJson(mRobotId))
    }

    fun getNavPoint(robotId: String, map: String) {
        val token = getToken()
        if (token.isNullOrEmpty() || robotId.isEmpty()) {
            return
        }
        getWuHanApiService().getNavigationPoint(token, robotId, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer <List<NavPointResponse>>{
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(body: List<NavPointResponse>) {
                    Log.d(TAG, "NavPointResponse: " + GsonUtils.toJson(body))
                    //parseRobotAllResponseBody(body)
                }

                override fun onError(e: Throwable) {
                    Log.w(TAG,"onError: ${e.message}")
                }

                override fun onComplete() {
                }
            })
    }

    @SuppressLint("CheckResult")
    fun navigatePoint(id: String, positionName: String, z: Int, flag: Int, mapinfo: String) {
        val token = getToken()
        if (token.isNullOrEmpty()) {
            return
        }
        val jsonObject = JsonObject();
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("positionName", positionName)
        jsonObject.addProperty("z", z)
        jsonObject.addProperty("flag", flag)
        jsonObject.addProperty("mapinfo", mapinfo)
        val body = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        getWuHanApiService().singlePointNavigate(body, token)
            .timeout(SERVER_TIMEOUT, TimeUnit.MILLISECONDS)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {}
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

    private fun getOkHttpClient(): OkHttpClient {
        return getOkHttpClient(TAG)
    }

    private fun getOkHttpClient(tag: String, reLoginWhenTokenInvalid: Boolean = false): OkHttpClient {
        val builder = RetrofitUrlManager.getInstance().with(OkHttpClient().newBuilder())
        //设置 请求的缓存的大小跟位置
        try {
            builder.run {
                if (reLoginWhenTokenInvalid) {
                    addInterceptor(TokenInterceptor {
                        login()
                    })
                }
                connectTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                retryOnConnectionFailure(true) // 错误重连
                proxy(Proxy.NO_PROXY) //防止被抓包，提升安全性
                // cookieJar(CookieManager())
                Log.d(TAG, "getOkHttpClient")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getOkHttpClient Exception：$e")
        }
        return builder.build()
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
        if (initState == HttpConstant.INIT_STATE_GET_IP && isGetUserAndPwm) {
            Log.d(TAG, "login condition is get")
        }
        //这里条件(账户名和密码)都满足后才开始登录
        initState = HttpConstant.INIT_STATE_GET_USER
        val root = JSONObject()
        root.put("username", getWuHanUserName())
        root.put("password", getWuHanPassWord())

        val requestBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), root.toString())
        Log.d(TAG, "ready login wuhan Server, requestBody: $requestBody")
        return requestBody
    }

    private fun parseLoginResponseBody(loginResponseBody: ResponseBody): Boolean {
        var token = ""
        var projectId = ""
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
            // 设置token过期时长
            val timeOut = obj.getLong("timeout") * 1000
            val createTime = obj.getLong("createtime")
            setTokenInvalidTime(timeOut + createTime)
        } catch (e: Exception) {
            Log.d(TAG, "parse WuHanLogin Exception:$e")
        }
        initState = HttpConstant.INIT_STATE_LONGIN_GET_TOKEN
        if (TextUtils.isEmpty(token)) {
            Log.d(TAG, "get Token is Empty")
            return false
        }
        setToken(token)
        setProject(projectId)
        Log.w(TAG, "requestWuHanLogin getToken:${getToken()}")
        return true
    }

    var mHandler = Handler(Looper.getMainLooper())
    /**
     * token无效时间
     */
    private fun setTokenInvalidTime(tokenInvalidTime: Long) {
        mTokenInvalidTime.set(tokenInvalidTime)
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

    fun setRobotList(robotId: List<String>) {
        //设备id
        Log.d(TAG, "RetrofitManager setRobotId=$robotId")
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

    fun addSubscription(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }
}