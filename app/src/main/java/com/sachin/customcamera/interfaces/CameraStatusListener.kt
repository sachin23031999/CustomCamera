package com.sachin.customcamera.interfaces

import android.net.Uri

interface CameraStatusListener {
    fun onImageSaved(uri: Uri)
    fun onError(error: String?)
}