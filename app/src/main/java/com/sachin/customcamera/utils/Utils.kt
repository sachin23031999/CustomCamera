package com.sachin.customcamera.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Utils {
    companion object {

        fun navigateTo(activity: FragmentActivity, viewId: Int, fragment: Fragment, tag: String) {
            val fragmentTransaction: FragmentTransaction =
                activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(viewId, fragment, tag)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fun bitmapToBase64(bitmap: Bitmap): String {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        }
        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                return BitmapFactory.decodeStream(inputStream)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                // Close the input stream
                inputStream?.close()
            }
            return null
        }

        fun deleteFolder(folderPath: String): Boolean {
            return try {
                val folder = File(folderPath)
                if (folder.isDirectory) {
                    val files = folder.listFiles()
                    files.forEach { it.delete() }
                }
                else
                    false

                folder.delete()
                true
            } catch (ex: java.lang.Exception) {
                false
            }

        }
        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }

    }

}