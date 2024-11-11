package com.wutsi.koki.portal.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractPageController {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }
}
