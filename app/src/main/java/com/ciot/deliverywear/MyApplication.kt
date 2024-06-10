package com.ciot.deliverywear

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.utils.ContextUtil
import java.util.LinkedList


class MyApplication : MultiDexApplication() {
    var mActivityList = LinkedList<Activity>()

    companion object {
        val TAG = "MyApplication"
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
    override fun onCreate() {
        Log.d(ConstantLogic.TIME_TEST, "MyApplication onCreate start")
        super.onCreate()
        context = this
        ContextUtil.setContext(this)
    }
}