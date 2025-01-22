package com.wutsi.koki.portal.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractPageController {
    companion object {
        const val TOAST_TTL = 10L * 1000 // 10 secs
    }

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

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

    protected fun toErrorResponse(ex: HttpClientErrorException): ErrorResponse {
        return objectMapper.readValue(ex.responseBodyAsString, ErrorResponse::class.java)
    }

    protected fun canShowToasts(
        timestamp: Long?,
        referer: String? = null,
        allowedReferers: List<String> = emptyList()
    ): Boolean {
        if (timestamp == null || referer == null || System.currentTimeMillis() - timestamp > TOAST_TTL) {
            return false
        }

        allowedReferers.forEach { ref ->
            if (referer.contains(ref)) {
                return true
            }
        }

        return false
    }
}
