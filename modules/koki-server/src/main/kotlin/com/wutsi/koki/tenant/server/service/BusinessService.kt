package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.JuridictionService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import com.wutsi.koki.tenant.server.dao.BusinessRepository
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class BusinessService(
    private val dao: BusinessRepository,
    private val securityService: SecurityService,
    private val juridictionService: JuridictionService,
    private val locationService: LocationService,
) {
    fun get(tenantId: Long): BusinessEntity {
        return dao.findByTenantId(tenantId)
            ?: throw NotFoundException(
                error = Error(ErrorCode.BUSINESS_NOT_FOUND)
            )
    }

    @Transactional
    fun save(request: SaveBusinessRequest, tenantId: Long) {
        val business = dao.findByTenantId(tenantId)
        if (business == null) {
            create(request, tenantId)
        } else {
            update(request, business)
        }
    }

    private fun create(request: SaveBusinessRequest, tenantId: Long): BusinessEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val city = request.addressCityId?.let { id -> locationService.get(id, LocationType.CITY) }
        val now = Date()
        return dao.save(
            BusinessEntity(
                tenantId = tenantId,
                companyName = request.companyName,
                email = request.email,
                phone = request.phone,
                fax = request.fax,
                website = request.website,
                addressCityId = city?.id,
                addressStateId = city?.parentId,
                addressStreet = request.addressStreet,
                addressCountry = request.addressCountry?.uppercase(),
                addressPostalCode = request.addressPostalCode,
                createdAt = now,
                createdById = userId,
                modifiedAt = now,
                modifiedById = userId,
                juridictions = if (request.juridictionIds.isNotEmpty()) {
                    juridictionService.search(
                        ids = request.juridictionIds.distinct(),
                        limit = request.juridictionIds.size
                    ).toMutableList()
                } else {
                    mutableListOf()
                }
            )
        )
    }

    private fun update(request: SaveBusinessRequest, business: BusinessEntity) {
        val city = request.addressCityId?.let { id -> locationService.get(id, LocationType.CITY) }

        business.companyName = request.companyName
        business.email = request.email
        business.phone = request.phone
        business.fax = request.fax
        business.website = request.website
        business.addressCityId = city?.id
        business.addressStateId = city?.parentId
        business.addressStreet = request.addressStreet
        business.addressCountry = request.addressCountry?.uppercase()
        business.addressPostalCode = request.addressPostalCode
        business.modifiedAt = Date()
        business.modifiedById = securityService.getCurrentUserIdOrNull()
        if (request.juridictionIds.isEmpty()) {
            business.juridictions.clear()
        } else {
            business.juridictions = juridictionService.search(
                ids = request.juridictionIds.distinct(),
                limit = request.juridictionIds.size
            ).toMutableList()
        }
        dao.save(business)
    }
}
