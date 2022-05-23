package com.chemist.youngchemist.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class Vibration {
    companion object {
        fun makeSmallVibration(context: Context) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK))
            } else {
                v?.vibrate(50)
            }
        }
    }
}