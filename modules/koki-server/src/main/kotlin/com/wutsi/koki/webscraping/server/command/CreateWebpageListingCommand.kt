package com.wutsi.koki.webscraping.server.command

data class CreateWebpageListingCommand(
    val webpageId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
)
