package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotBlank

data class LinkListingVideoRequest(
    @get:NotBlank val videoUrl: String = "",
)
