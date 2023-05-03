package com.sachin.customcamera.interfaces

import android.net.Uri

interface OnCaptureListener {
    fun onStart(exposure: Long)
    fun onSaved(exposure: Long, uri: Uri)
    fun onComplete()
    fun onError(error: String?)
}