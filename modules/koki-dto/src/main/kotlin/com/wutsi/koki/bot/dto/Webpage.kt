package com.wutsi.koki.bot.dto

data class Webpage(
    var url: String = "",
    var content: String = "",
    var imageUrls: List<String> = emptyList(),
)
