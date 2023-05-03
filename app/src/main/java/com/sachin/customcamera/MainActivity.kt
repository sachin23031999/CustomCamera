package com.sachin.customcamera

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.sachin.customcamera.fragments.OnboardFragment
import com.sachin.customcamera.utils.Utils
import com.sachin.customcamera.utils.Constants.REQUEST_SPECIFIED_PERMISSION


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            proceedToFragment()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), REQUEST_SPECIFIED_PERMISSION)
        }




    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_SPECIFIED_PERMISSION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                proceedToFragment()

            } else
                Toast.makeText(this, getString(R.string.grant_permission), Toast.LENGTH_SHORT).show()

        }

    }
    private fun proceedToFragment() = Utils.navigateTo(
        this,
        R.id.fragment_container,
        OnboardFragment(),
        ""
    )

}