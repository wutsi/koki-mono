package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.file.dto.ImageQuality

data class ListingImageReviewerAgentResult(
    val title: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val descriptionFr: String? = null,
    val quality: ImageQuality? = null,
    val valid: Boolean = false,
    val reason: String? = null,
)
