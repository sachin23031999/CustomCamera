package com.sachin.customcamera.network

import com.sachin.customcamera.utils.Constants.BASE_URL
import com.sachin.customcamera.utils.Constants.RETROFIT_WRITE_TIMEOUT
import com.sachin.customcamera.utils.Constants.RETROFIT_CONNECT_TIMEOUT
import com.sachin.customcamera.utils.Constants.RETROFIT_READ_TIMEOUT
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    fun getInstance(): Retrofit {

        val client = OkHttpClient.Builder()
            .connectTimeout(RETROFIT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(RETROFIT_READ_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(RETROFIT_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}