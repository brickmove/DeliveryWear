package com.ciot.deliverywear.network;

import android.text.TextUtils;

import com.ciot.deliverywear.RetrofitManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 全局自动刷新Token的拦截器
 */
public class TokenInterceptor implements Interceptor {

    public TokenInterceptor(OnTokenInvalidListener onTokenInvalidListener) {
        mOnTokenInvalidListener = onTokenInvalidListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (mOnTokenInvalidListener != null && isTokenExpired(response)) {//根据和服务端的约定判断token过期
            mOnTokenInvalidListener.onTokenInvalidListener();
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     */
    private boolean isTokenExpired(Response response) {
        // 没有用户名时，无法重新登录；所以不算token失效
        return response.code() == 401 && !TextUtils.isEmpty(RetrofitManager.Companion.getInstance().getWuHanUserName());
    }

    private final OnTokenInvalidListener mOnTokenInvalidListener;

    public interface OnTokenInvalidListener {
        void onTokenInvalidListener();
    }
}