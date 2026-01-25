package com.wutsi.koki.listing.dto

data class GenerateQrCodeResponse(
    val listingId: Long = -1,
    val qrCodeUrl: String = ""
)
