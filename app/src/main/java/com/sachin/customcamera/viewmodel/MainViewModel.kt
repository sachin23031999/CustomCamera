package com.sachin.customcamera.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.ViewModel
import com.sachin.customcamera.CameraHelper
import com.sachin.customcamera.interfaces.CameraStatusListener
import com.sachin.customcamera.interfaces.OnCaptureListener
import com.sachin.customcamera.repo.Repository
import com.sachin.customcamera.utils.Utils
import com.sachin.customcamera.utils.Constants
import com.sachin.customcamera.utils.Logger.Companion.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: Repository): ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    fun captureSingle(context: Context, focus: Float? = null, iso: Int? = null, exposure: Long? = null) {
        d(TAG, "captureSingle()")
        val handlerThread = HandlerThread("camS")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            CameraHelper(context)
                .withIso(iso)
                .withFocus(focus)
                .withExposure(exposure)
                .captureImage()
        }
    }

    fun captureMultiple(context: Context, focus: Float? = null, iso: Int? = null, exposure: Long, exposureU: Long, listener: OnCaptureListener) {
        d(TAG, "captureMultiple()")
        if(exposure > exposureU) {
            listener.onComplete()
            return
        }
        val handlerThread = HandlerThread("camM")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
                listener.onStart(exposure)
                CameraHelper(context)
                    .withIso(iso)
                    .withFocus(focus)
                    .withExposure(exposure)
                    .withListener(object : CameraStatusListener {
                        override fun onImageSaved(uri: Uri) {
                            listener.onSaved(exposure, uri)
                            if(exposure <= exposureU)
                                captureMultiple(context, focus, iso, exposure+1, exposureU, listener)
                        }

                        override fun onError(error: String?) {
                            listener.onError(error)
                        }

                    })
                    .captureImage()
            }
    }

    fun saveInfo(name: String, email: String) {
        d(TAG, "saveInfo()")
        repository.save("name", name)
        repository.save("email", email)
    }

    fun getInfo(): Pair<String?, String?> {
        d(TAG, "getInfo()")
        return repository.getInfo()
    }

    fun sendImage(uri: Uri, callback:(sent: Boolean) -> Unit) {
        d(TAG, "sendImage()")
        CoroutineScope(Dispatchers.IO).launch {
            val login = repository.login(Constants.ID, Constants.PASS)
            if(login.isOnboarded)
                callback(
                    repository.sendImage(login, uri)
                )
        }
    }

    fun deleteImageFolder() {
        d(TAG, "deleteImageFolder()")
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES + File.separator + Constants.IMAGES_FOLDER_NAME
        Utils.deleteFolder(folderPath)
    }

}
