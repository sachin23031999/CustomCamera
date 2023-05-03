package com.sachin.customcamera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.sachin.customcamera.interfaces.CameraStatusListener
import com.sachin.customcamera.utils.Constants.IMAGES_FOLDER_NAME
import com.sachin.customcamera.utils.Logger.Companion.d
import com.sachin.customcamera.utils.Logger.Companion.e
import java.io.File

class CameraHelper(private val mContext: Context) {

    private val TAG = CameraHelper::class.java.simpleName
    private var mCameraManager: CameraManager? = null
    private var mCameraId: String? = null
    private var mCamCharacteristics: CameraCharacteristics? = null
    private var mFocus: Float? = null
    private var mIso: Int? = null
    private var mExposure: Long? = null
    private var mListener: CameraStatusListener? = null

    init {
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraId = mCameraManager?.cameraIdList?.get(0)
        mCamCharacteristics = mCameraManager?.getCameraCharacteristics(mCameraId!!)
    }

    fun withFocus(focus: Float?) = apply { this.mFocus = focus }
    fun withIso(iso: Int?) = apply { this.mIso = iso }
    fun withExposure(exposure: Long?) = apply { this.mExposure = exposure }
    fun withListener(listener: CameraStatusListener) = apply { this.mListener = listener }

    fun captureImage(){
        val imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1)

        try {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            mCameraManager?.openCamera(mCameraId!!, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    val surface = imageReader.surface
                    val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                    captureRequestBuilder.addTarget(surface)

                    if(mExposure != null)
                        captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, mExposure)
                    if(mIso != null)
                        captureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, mIso)
                    if(mFocus != null)
                        captureRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, mFocus)
                    captureRequestBuilder.build()
                    val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                        override fun onCaptureCompleted(session: CameraCaptureSession,
                                                        request: CaptureRequest,
                                                        result: TotalCaptureResult
                        ) {
                            d(TAG, "Image captured successfully")
                            //imageReader.close()
                            camera.close()

                            val buffer = imageReader.acquireLatestImage().planes[0].buffer
                            val bytes = ByteArray(buffer.capacity())
                            buffer.get(bytes)
                            imageReader.close()
                            writeImage(bytes)
                        }
                    }

                    camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            session.capture(captureRequestBuilder.build(), captureCallback, null)
                            d(TAG, "onConfigured success")
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            e(TAG, "Failed to configure camera capture session")
                        }
                    }, null)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    e(TAG, "Camera disconnected")
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    e(TAG, "Error opening camera")
                    mListener?.onError("Error opening camera")
                }
            }, null)
        } catch (e: CameraAccessException) {
            e(TAG, "Error accessing camera, $e")
            mListener?.onError(e.message)
        }
    }

    private fun writeImage(bytes: ByteArray) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "MyImage.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + IMAGES_FOLDER_NAME)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = mContext.applicationContext.contentResolver
        var uri: Uri?
        resolver.run {
            uri = insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                try {
                    val outputStream = resolver.openOutputStream(it)
                    outputStream?.use { it.write(bytes) }
                } catch (e: Exception) {
                    e(TAG, "Error writing image to gallery, $e")
                    mListener?.onError(e.message)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                update(uri!!, contentValues, null, null)
            }
        }
        mListener?.onImageSaved(uri!!)
        d(TAG, "Image saved to gallery: $uri")
    }


}

