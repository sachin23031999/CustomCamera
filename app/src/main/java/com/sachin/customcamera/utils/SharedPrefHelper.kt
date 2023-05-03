package com.sachin.customcamera.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper private constructor(){

    private val TAG = "SharedPrefHelper"
    companion object {
        private var sharedPrefHelper = SharedPrefHelper()
        private lateinit var sharedPref: SharedPreferences
        fun getInstance(context: Context?) : SharedPrefHelper {
            if(!Companion::sharedPref.isInitialized) {
                synchronized(SharedPrefHelper::class.java) {
                    if(!Companion::sharedPref.isInitialized) {
                        if (context != null) {
                            sharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
                        }
                    }
                }
            }
            return sharedPrefHelper
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPref.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun putString(key: String, value: String?) {
        sharedPref.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }
}
