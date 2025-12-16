package com.wutsi.koki.portal.pub.lead.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.Lead
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.portal.pub.lead.form.LeadForm
import com.wutsi.koki.portal.pub.lead.mapper.LeadMapper
import com.wutsi.koki.portal.pub.lead.model.LeadModel
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.pub.user.service.CurrentUserHolder
import com.wutsi.koki.portal.pub.user.service.UserIdProvider
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.sdk.KokiLeads
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeadService(
    private val koki: KokiLeads,
    private val mapper: LeadMapper,
    private val userIdProvider: UserIdProvider,
    private val userHolder: CurrentUserHolder,
    private val tenantHolder: CurrentTenantHolder,
    private val servletRequest: HttpServletRequest,
    private val ipService: GeoIpService,
    private val locationService: LocationService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LeadService::class.java)
    }

    fun create(form: LeadForm): Long {
        // Create
        val email = form.email.ifEmpty { null } ?: userHolder.get()?.email ?: ""
        val city = resolveCity()
        val request = CreateLeadRequest(
            listingId = form.listingId,
            agentUserId = form.agentUserId,
            firstName = form.firstName,
            lastName = form.lastName,
            message = form.message,
            email = email,
            phoneNumber = form.phoneFull,
            source = form.listingId?.let { LeadSource.LISTING } ?: LeadSource.AGENT,
            userId = userIdProvider.get(),
            cityId = city?.id,
            country = city?.country ?: resolveCountryFromPhone(form.phoneFull),
        )
        val leadId = koki.create(request).leadId

        // Set current user
        val lead = koki.get(leadId).lead
        userIdProvider.set(lead.userId)

        return leadId
    }

    fun get(id: Long): LeadModel {
        val lead: Lead = koki.get(id).lead
        return mapper.toLeadModel(lead)
    }

    protected fun getIp(request: HttpServletRequest): String {
        return request.getHeader("X-FORWARDED-FOR")?.ifEmpty { null } ?: request.remoteAddr
    }

    private fun resolveCity(): LocationModel? {
        val ip = getIp(servletRequest)
        try {
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
            LOGGER.warn("Unable to resolve city from $ip", ex)
            return null
        }
    }

    private fun resolveCountryFromPhone(phone: String): String? {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val defaultCountryCode = tenantHolder.get().country
        return try {
            val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(phone, defaultCountryCode)
            if (phoneUtil.isValidNumber(numberProto)) {
                return phoneUtil.getRegionCodeForNumber(numberProto)
            } else {
                // Handle invalid number case
                null
            }
        } catch (e: com.google.i18n.phonenumbers.NumberParseException) {
            // Handle parsing errors
            println("Error parsing phone number: ${e.message}")
            null
        }
    }
}
