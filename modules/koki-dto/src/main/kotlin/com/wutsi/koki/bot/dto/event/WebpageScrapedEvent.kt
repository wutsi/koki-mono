package com.wutsi.koki.bot.dto.event

data class WebpageScrapedEvent(
    val website: String = "",
    val url: String = "",
    val contentUrl: String = "",
)
