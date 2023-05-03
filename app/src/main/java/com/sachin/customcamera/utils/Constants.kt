package com.sachin.customcamera.utils

object Constants {
    const val APP_NAME = "CustomCamera"

    //params
    const val EXPOSURE_L = -12L
    const val EXPOSURE_U = 12L
    const val COUNTDOWN_TIME = 301000L // Timer starts after 1 sec that's why put 301 instead of 300
    const val PERIOD = 5000L

    const val BASE_URL = "http://apistaging.inito.com"

    const val REQUEST_SPECIFIED_PERMISSION = 1

    const val IMAGES_FOLDER_NAME = "CustomCamera"

    //auth credentials
    const val ID = "amit_4@test.com"
    const val PASS = "12345678"

    //sign-in response headers
    const val ACCESS_TOKEN = "access-token"
    const val UID = "uid"
    const val CLIENT = "client"

    //network request timeout in seconds
    const val RETROFIT_CONNECT_TIMEOUT = 10L
    const val RETROFIT_READ_TIMEOUT = 10L
    const val RETROFIT_WRITE_TIMEOUT = 20L
}