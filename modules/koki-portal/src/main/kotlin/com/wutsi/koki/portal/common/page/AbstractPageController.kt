package com.wutsi.koki.portal.common.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.portal.common.service.Toggles
import com.wutsi.koki.portal.common.service.TogglesHolder
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

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

    @Autowired
    protected lateinit var togglesHolder: TogglesHolder

    @ModelAttribute("user")
    fun getUser(): UserModel? {
        return userHolder.get()
    }

    @ModelAttribute("tenant")
    fun getTenant(): TenantModel? {
        return tenantHolder.get()
    }

    @ModelAttribute("toggles")
    fun getToggles(): Toggles {
        return togglesHolder.get()
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

    protected fun loadLanguages(model: Model, name: String? = "languages") {
        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .toSet()
            .sortedBy { locale -> locale.getDisplayLanguage() }
        model.addAttribute(name, languages)
    }

    protected fun loadCountries(model: Model, name: String? = "countries") {
        val countries = Locale.getISOCountries()
            .map { country -> Locale(LocaleContextHolder.getLocale().language, country) }
            .sortedBy { country -> country.getDisplayCountry() }
        model.addAttribute(name, countries)
    }
}
