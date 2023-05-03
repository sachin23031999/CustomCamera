package com.sachin.customcamera.repo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sachin.customcamera.data.ImageRequest
import com.sachin.customcamera.data.LoginData
import com.sachin.customcamera.data.LoginRequest
import com.sachin.customcamera.network.Apis
import com.sachin.customcamera.network.RetrofitHelper
import com.sachin.customcamera.utils.Constants
import com.sachin.customcamera.utils.Logger.Companion.d
import com.sachin.customcamera.utils.SharedPrefHelper
import com.sachin.customcamera.utils.Utils
import java.net.HttpURLConnection

class Repository(private val mContext: Context) {

    private val TAG = Repository::class.java.simpleName
    private val sharedPrefHelper = SharedPrefHelper.getInstance(mContext)
    private val apis = RetrofitHelper.getInstance().create(Apis::class.java)

    fun save(key: String, value: Any?) {
        if(value is String)
            sharedPrefHelper.putString(key, value)
        else if (value is Boolean)
            sharedPrefHelper.putBoolean(key, value)
    }

    fun getInfo(): Pair<String?, String?> {
        val name = sharedPrefHelper.getString("name")
        val email = sharedPrefHelper.getString("email")
        return Pair(name, email)
    }

    suspend fun login(emailId: String, password: String): LoginData {
        val response = apis.login(LoginRequest(emailId, password))
        val headers = response.headers()

        return LoginData(
            accessToken = headers.get(Constants.ACCESS_TOKEN),
            uid = headers.get(Constants.UID),
            client = headers.get(Constants.CLIENT),
            isOnboarded = response.body()?.onboarded?:false
        )
    }

    suspend fun sendImage(loginData: LoginData, uri: Uri): Boolean {
        val bitmap = Utils.getBitmapFromUri(mContext, uri)
        val base64String = bitmap?.let { Utils.bitmapToBase64(it) }
        val imageRequest = ImageRequest(Utils.getCurrentDate(), base64String!!, null, null, false)

        val response = apis.postImage(
            loginData.accessToken,
            loginData.uid,
            loginData.client,
            imageRequest
        )
        if(response.code() == HttpURLConnection.HTTP_OK)
            return true

        return false
    }
}