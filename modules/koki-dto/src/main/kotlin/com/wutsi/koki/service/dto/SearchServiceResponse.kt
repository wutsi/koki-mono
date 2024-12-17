package com.wutsi.koki.service.dto

data class SearchServiceResponse(
    val services: List<ServiceSummary> = emptyList()
)
