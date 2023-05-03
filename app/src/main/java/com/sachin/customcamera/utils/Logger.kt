package com.sachin.customcamera.utils

import android.util.Log
import com.sachin.customcamera.utils.Constants.APP_NAME
import com.sachin.customcamera.utils.Utils

class Logger {
    companion object {
        fun d(tag: String, message: String) {
            Log.d("$APP_NAME [$tag]", message)
        }
        fun e(tag: String, message: String) {
            Log.e("$APP_NAME[$tag]", message)
        }
        fun i(tag: String, message: String) {
            Log.i("$APP_NAME [$tag]", message)
        }
    }
}