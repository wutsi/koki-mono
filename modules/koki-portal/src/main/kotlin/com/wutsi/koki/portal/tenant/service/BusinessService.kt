package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.portal.refdata.service.JuridictionService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.tenant.form.BusinessForm
import com.wutsi.koki.portal.tenant.mapper.TenantMapper
import com.wutsi.koki.portal.tenant.model.BusinessModel
import com.wutsi.koki.sdk.KokiBusinesses
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import org.springframework.stereotype.Service

@Service
class BusinessService(
    private val koki: KokiBusinesses,
    private val mapper: TenantMapper,
    private val juridictionService: JuridictionService,
    private val locationService: LocationService,
) {
    fun business(): BusinessModel {
        val business = koki.business().business

        val juridictions = juridictionService.juridictions(
            ids = business.juridictionIds,
            limit = business.juridictionIds.size
        ).associateBy { juridiction -> juridiction.id }

        val locationIds = listOf(business.address?.cityId, business.address?.stateId).filterNotNull()
        val locations = if (locationIds.isEmpty()) {
            emptyMap()
        } else {
            locationService.search(
                ids = locationIds,
                limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        return mapper.toBusinessModel(
            entity = business,
            locations = locations,
            juridictions = juridictions
        )
    }

    fun save(form: BusinessForm) {
        koki.save(
            SaveBusinessRequest(
                companyName = form.companyName,
                email = form.email,
                fax = form.fax,
                phone = form.phone,
                website = form.website,
                addressCityId = form.addressCityId,
                addressStreet = form.addressStreet,
                addressCountry = form.addressCountry,
                addressPostalCode = form.addressPostalCode,
                juridictionIds = form.juridictionIds,
            )
        )
    }
}
