package com.wutsi.koki.bot.server.domain

data class WebpageEntity(
    var url: String = "",
    var content: String = "",
    var imageUrls: List<String> = emptyList(),
)
