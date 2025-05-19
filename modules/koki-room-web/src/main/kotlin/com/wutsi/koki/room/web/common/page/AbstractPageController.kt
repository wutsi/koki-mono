package com.wutsi.koki.room.web.common.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.web.common.model.PageModel
import com.wutsi.koki.room.web.tenant.model.TenantModel
import com.wutsi.koki.room.web.tenant.service.CurrentTenantHolder
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
    protected lateinit var tenantHolder: CurrentTenantHolder

    @ModelAttribute("tenant")
    fun getTenant(): TenantModel? {
        return tenantHolder.get()
    }

    protected open fun createPageModel(name: String, title: String, description: String? = null): PageModel {
        return PageModel(
            name = name,
            title = title,
            description = description,
            assetUrl = assetUrl,
        )
    }

    protected fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }
}
