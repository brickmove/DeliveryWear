package com.ciot.deliverywear.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

object VibrationUtils {
    private val lock = Object()

    fun vibrate(context: Context, duration: Long) {
        if (hasVibratorFeature(context)) {
            synchronized(lock) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    fun cancelVibration(context: Context) {
        if (hasVibratorFeature(context)) {
            synchronized(lock) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.cancel()
            }
        }
    }

    fun hasVibratorFeature(context: Context): Boolean {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        return vibrator.hasVibrator()
    }
}
