package com.wutsi.koki.room.server.server.agent

import com.wutsi.koki.file.dto.ImageQuality

data class ImageReviewerAgentResult(
    val title: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val descriptionFr: String? = null,
    val quality: ImageQuality? = null,
    val valid: Boolean = false,
    val reason: String? = null,
)
