package com.wutsi.koki.portal.common.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.service.Toggles
import com.wutsi.koki.portal.common.service.TogglesHolder
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.portal.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import com.wutsi.koki.refdata.dto.LocationType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
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

    @Value("\${koki.webapp.asset-url}")
    protected lateinit var assetUrl: String

    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var request: HttpServletRequest

    @Autowired
    protected lateinit var ipService: GeoIpService

    @Autowired
    protected lateinit var locationService: LocationService

    @Autowired
    protected lateinit var logger: KVLogger

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

    protected open fun createPageModel(name: String, title: String): PageModel {
        return PageModel(
            name = name,
            title = title,
            assetUrl = assetUrl,
        )
    }

    protected fun getMessage(key: String, args: Array<Any?>? = null, locale: Locale? = null): String {
        try {
            val loc = locale ?: LocaleContextHolder.getLocale()
            return messages.getMessage(key, args, loc)
        } catch (ex: Exception) {
            return key
        }
    }

    protected fun getIp(request: HttpServletRequest): String {
        return request.getHeader("X-FORWARDED-FOR")?.ifEmpty { null } ?: request.remoteAddr
    }

    protected fun resolveCity(): LocationModel? {
        try {
            val ip = getIp(request)
            val geo = ipService.resolve(ip)
            return if (geo != null) {
                locationService.search(
                    country = geo.countryCode,
                    keyword = geo.city,
                    types = listOf(LocationType.CITY),
                    limit = 1,
                ).firstOrNull()
            } else {
                null
            }
        } catch (ex: Exception) {
            return null
        }
    }

    protected fun resolveParent(city: LocationModel?): LocationModel? {
        return city?.parentId?.let { id -> locationService.get(id) }
    }

    protected fun loadError(ex: HttpClientErrorException, model: Model) {
        val response = toErrorResponse(ex)
        logger.add("backend_error", response.error.code)
        loadError(response, model)
    }

    protected fun loadError(response: ErrorResponse, model: Model) {
        val error = toErrorMessage(response)
        model.addAttribute("error", error)
    }

    protected open fun toErrorMessage(response: ErrorResponse): String {
        return when (response.error.code) {
            ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE -> getMessage("error.account-not-active")
            ErrorCode.AUTHENTICATION_FAILED -> getMessage("error.authentication-failed")

            ErrorCode.LISTING_MISSING_ADDRESS -> getMessage("error.listing-missing-address")
            ErrorCode.LISTING_MISSING_APPROVED_IMAGE -> getMessage("error.listing-missing-approved-image")
            ErrorCode.LISTING_MISSING_GEOLOCATION -> getMessage("error.listing-missing-geolocation")
            ErrorCode.LISTING_MISSING_PRICE -> getMessage("error.listing-missing-price")
            ErrorCode.LISTING_MISSING_SELLER -> getMessage("error.listing-missing-seller")
            ErrorCode.LISTING_MISSING_SELLER_COMMISSION -> getMessage("error.listing-missing-seller-commission")
            ErrorCode.LISTING_INVALID_BUYER_COMMISSION -> getMessage("error.listing-invalid-buyer-commission")
            ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_HOUSE -> getMessage("error.listing-missing-general-info-house")
            ErrorCode.LISTING_MISSING_GENERAL_INFORMATION_LAND -> getMessage("error.listing-missing-general-info-land")

            ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED -> getMessage("error.password-reset-expired")
            ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND -> getMessage("error.password-reset-not-found")

            ErrorCode.USER_DUPLICATE_EMAIL -> getMessage("error.user.duplicate-email")
            ErrorCode.USER_DUPLICATE_USERNAME -> getMessage("error.user.duplicate-username")
            ErrorCode.USER_NOT_FOUND -> getMessage("error.user.not-found")
            else -> getMessage("error.unexpected-error")
        }
    }
}
