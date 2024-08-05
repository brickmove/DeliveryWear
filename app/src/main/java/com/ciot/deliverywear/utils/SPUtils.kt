package com.ciot.deliverywear.utils

import android.content.Context
import android.content.SharedPreferences

// 存储绑定信息
class SPUtils() {
    private val pref: SharedPreferences =
        ContextUtil.getContext().getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()
    @Volatile
    private var INSTANCE: SPUtils? = null
    fun getInstance(): SPUtils {
        if (INSTANCE == null) {
            synchronized(SPUtils::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = SPUtils()
                }
            }
        }
        return INSTANCE!!
    }

    companion object {
        private const val TAG = "PrefManager"
        private const val MAX_RETRIES = 3
        private const val DEFAULT_SP_NAME = "DELIVERY_SP_DATA"
    }

    fun putBoolean(key: String, value: Boolean, isCommit: Boolean) {
        if (isCommit) {
            var success = false
            var attempts = 0
            while (!success && attempts < MAX_RETRIES) {
                editor.putBoolean(key, value)
                success = editor.commit()
                if (success) {
                    MyLog.d(TAG, "Set bool value to $value successfully")
                } else {
                    MyLog.d(TAG, "Failed to set bool value to $value. Attempt ${attempts + 1} of $MAX_RETRIES")
                }
                attempts++
            }
        } else {
            editor.putBoolean(key, value)
            editor.apply()
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        this.putBoolean(key, value, false)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return pref.getBoolean(key, defaultValue)
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun putString(key: String, value: String, isCommit: Boolean) {
        if (isCommit) {
            var success = false
            var attempts = 0
            while (!success && attempts < MAX_RETRIES) {
                editor.putString(key, value)
                success = editor.commit()
                if (success) {
                    MyLog.d(TAG, "Set string value to $value successfully")
                } else {
                    MyLog.d(TAG, "Failed to set string value to $value. Attempt ${attempts + 1} of $MAX_RETRIES")
                }
                attempts++
            }
        } else {
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun putString(key: String, value: String) {
        this.putString(key, value, false)
    }

    fun getString(key: String, defaultValue: String): String? {
        return pref.getString(key, defaultValue)
    }

    fun getString(key: String): String? {
        return this.getString(key, "")
    }

    fun putInt(key: String, value: Int, isCommit: Boolean) {
        if (isCommit) {
            var success = false
            var attempts = 0
            while (!success && attempts < MAX_RETRIES) {
                editor.putInt(key, value)
                success = editor.commit()
                if (success) {
                    MyLog.d(TAG, "Set int value to $value successfully")
                } else {
                    MyLog.d(TAG, "Failed to set int value to $value. Attempt ${attempts + 1} of $MAX_RETRIES")
                }
                attempts++
            }
        } else {
            editor.putInt(key, value)
            editor.apply()
        }
    }

    fun putInt(key: String, value: Int) {
        this.putInt(key, value, false)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return pref.getInt(key, defaultValue)
    }

    fun getInt(key: String): Int {
        return getInt(key, -1)
    }

    fun contains(key: String): Boolean {
        return pref.contains(key)
    }
}
