package com.wutsi.koki.room.server.service.data

data class ImageAgentData(
    val title: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val descriptionFr: String? = null,
    val hashtags: List<String>? = null,
    val quality: Int = 0,
    val valid: Boolean = false,
    val reason: String? = null,
)
