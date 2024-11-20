package com.wutsi.koki.portal.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.portal.service.CurrentUserHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractPageController {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var userHolder: CurrentUserHolder

    @ModelAttribute("user")
    fun getUser(): UserModel? {
        return userHolder.get()
    }

    fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }
}
