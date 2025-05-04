package com.wutsi.koki.room.dto

data class AddImageRequest(
    val fileIds: List<Long> = emptyList()
)
