package com.ciot.deliverywear.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

// 存储绑定信息
class PrefManager(context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    companion object {
        private const val BIND_KEY = "bindKey"
        private const val PREF_NAME = "bind_pref"
        private const val IS_BOUND = "isBound"
        private const val IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch"
        private const val TAG = "PrefManager"
        private const val MAX_RETRIES = 3
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    var isBound: Boolean
        get() = pref.getBoolean(IS_BOUND, false)
        set(value) {
            var success = false
            var attempts = 0
            while (!success && attempts < MAX_RETRIES) {
                editor.putBoolean(IS_BOUND, value)
                success = editor.commit()
                if (success) {
                    Log.d(TAG, "Set bind value to $value successfully")
                } else {
                    Log.d(TAG, "Failed to set bind value to $value. Attempt ${attempts + 1} of $MAX_RETRIES")
                }
                attempts++
            }

            if (!success) {
                Log.e(TAG, "Failed to set bind value after $MAX_RETRIES attempts")
            }
        }

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(value) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, value)
            editor.apply()
        }

    var bindKey: String?
        get() = pref.getString(BIND_KEY, null)
        set(value) {
            var success = false
            var attempts = 0

            while (!success && attempts < MAX_RETRIES) {
                editor.putString(BIND_KEY, value)
                success = editor.commit()
                if (success) {
                    Log.d(TAG, "Set bind key to $value successfully")
                } else {
                    Log.d(TAG, "Failed to set bind key to $value. Attempt ${attempts + 1} of $MAX_RETRIES")
                }
                attempts++
            }

            if (!success) {
                Log.e(TAG, "Failed to set bind key after $MAX_RETRIES attempts")
            }
        }
}
