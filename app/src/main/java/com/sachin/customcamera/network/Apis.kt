package com.sachin.customcamera.network

import androidx.lifecycle.LiveData
import com.sachin.customcamera.data.ImageRequest
import com.sachin.customcamera.data.LoginRequest
import com.sachin.customcamera.data.LoginResponse
import com.sachin.customcamera.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface Apis {

    @POST("/api/v1/auth/sign_in")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/tests")
    suspend fun postImage(@Header(Constants.ACCESS_TOKEN) accessToken: String?,
                        @Header(Constants.UID) uid: String?,
                        @Header(Constants.CLIENT) client: String?,
                        @Body imageRequest: ImageRequest): Response<Any>
}