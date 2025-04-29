package com.wutsi.koki.portal.client.common.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.portal.client.common.model.PageModel
import com.wutsi.koki.portal.client.security.service.CurrentUserHolder
import com.wutsi.koki.portal.client.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.client.user.model.UserModel
import com.wutsi.koki.portal.tenant.model.TenantModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractPageController {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Value("\${koki.webapp.asset-url}")
    protected lateinit var assetUrl: String

    @Autowired
    protected lateinit var userHolder: CurrentUserHolder

    @Autowired
    protected lateinit var tenantHolder: CurrentTenantHolder

    @ModelAttribute("user")
    fun getUser(): UserModel? {
        return userHolder.get()
    }

    @ModelAttribute("tenant")
    fun getTenant(): TenantModel? {
        return tenantHolder.get()
    }

    protected open fun createPageModel(name: String, title: String): PageModel {
        return PageModel(
            name = name,
            title = title,
            assetUrl = assetUrl,
        )
    }

    protected fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }
}
