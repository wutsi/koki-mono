package com.wutsi.koki.service.server.service

import org.springframework.http.HttpStatusCode

data class ServiceResponse (
    val statusCode: HttpStatusCode,
    val body: Map<String, Any>?,
)
