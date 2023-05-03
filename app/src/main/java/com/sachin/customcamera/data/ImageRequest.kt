package com.sachin.customcamera.data

import java.util.Base64

data class ImageRequest(
    var done_date: String,
    var images_attributes: String,
    var batch_qr_code: String?,
    var reason: String?,
    var failure: Boolean
)
