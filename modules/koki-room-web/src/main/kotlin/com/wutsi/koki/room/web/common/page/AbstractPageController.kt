package com.wutsi.koki.room.web.common.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.web.common.model.PageModel
import com.wutsi.koki.room.web.tenant.model.TenantModel
import com.wutsi.koki.room.web.tenant.service.CurrentTenantHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

abstract class AbstractPageController {
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Value("\${koki.webapp.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${koki.webapp.base-url}")
    protected lateinit var baseUrl: String

    @Autowired
    protected lateinit var tenantHolder: CurrentTenantHolder

    @Autowired
    protected lateinit var messages: MessageSource

    @ModelAttribute("tenant")
    fun getTenant(): TenantModel? {
        return tenantHolder.get()
    }

    protected open fun createPageModel(
        name: String,
        title: String,
        description: String? = null,
        image: String? = null,
        url: String? = null,
    ): PageModel {
        return PageModel(
            name = name,
            title = title,
            description = description,
            image = image,
            url = url,
            assetUrl = assetUrl,
            language = LocaleContextHolder.getLocale().language
        )
    }

    protected fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }

    protected fun getMessage(key: String, args: Array<Any>? = null, locale: Locale? = null): String {
        try {
            val loc = locale ?: LocaleContextHolder.getLocale()
            return messages.getMessage(key, args, loc)
        } catch (ex: Exception) {
            return key
        }
    }
}
