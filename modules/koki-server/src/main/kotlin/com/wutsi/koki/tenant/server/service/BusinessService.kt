package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.refdata.server.service.JuridictionService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import com.wutsi.koki.tenant.server.dao.BusinessRepository
import com.wutsi.koki.tenant.server.dao.BusinessTaxIdentifierRepository
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.BusinessTaxIdentifierEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class BusinessService(
    private val dao: BusinessRepository,
    private val taxIdDao: BusinessTaxIdentifierRepository,
    private val salesTaxService: SalesTaxService,
    private val securityService: SecurityService,
    private val juridictionService: JuridictionService,
) {
    @Transactional
    fun save(request: SaveBusinessRequest, tenantId: Long) {
        val business = dao.findByTenantId(tenantId)
        if (business == null) {
            create(request, tenantId)
        } else {
            update(request, business)
        }
    }

    private fun create(request: SaveBusinessRequest, tenantId: Long) {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val business = dao.save(
            BusinessEntity(
                tenantId = tenantId,
                companyName = request.companyName,
                email = request.email,
                phone = request.phone,
                fax = request.fax,
                website = request.website,
                addressCityId = request.addressCityId,
                addressStreet = request.addressStreet,
                addressStateId = request.addressStateId,
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
        createTaxIdentifiers(business, request)
    }

    private fun createTaxIdentifiers(business: BusinessEntity, request: SaveBusinessRequest) {
        val salesTaxeIds = salesTaxService.search(
            juridictionIds = request.juridictionIds,
            limit = Integer.MAX_VALUE
        ).mapNotNull { salesTax -> salesTax.id }
            .toSet()
        salesTaxeIds
            .map { id ->
                val number = request.taxIdentifiers[id]?.trim()?.ifEmpty { null }
                number?.let {
                    taxIdDao.save(
                        BusinessTaxIdentifierEntity(
                            businessId = business.id,
                            salesTaxId = id,
                            number = number
                        )
                    )
                }
            }.filterNotNull()
    }

    private fun update(request: SaveBusinessRequest, business: BusinessEntity) {
        business.companyName = request.companyName
        business.email = request.email
        business.phone = request.phone
        business.fax = request.fax
        business.website = request.website
        business.addressCityId = request.addressCityId
        business.addressStreet = request.addressStreet
        business.addressStateId = request.addressStateId
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

        updateTaxIdentifiers(business, request)
    }

    private fun updateTaxIdentifiers(business: BusinessEntity, request: SaveBusinessRequest) {
        // Add/Update
        val taxIdentifierIds = mutableListOf<Long>()
        request.taxIdentifiers.forEach { entry ->
            var taxIdentifier = taxIdDao.findByBusinessIdAndSalesTaxId(business.id, entry.key)
            if (taxIdentifier == null) {
                taxIdentifier = taxIdDao.save(
                    BusinessTaxIdentifierEntity(
                        businessId = business.id,
                        salesTaxId = entry.key,
                        number = entry.value,
                    )
                )
            } else {
                taxIdentifier.number = entry.value
                taxIdDao.save(taxIdentifier)
            }

            taxIdentifierIds.add(taxIdentifier.id)
        }

        // Delete
        taxIdDao.findByBusinessId(business.id).forEach { taxIdentifier ->
            if (!taxIdentifierIds.contains(taxIdentifier.id)) {
                taxIdDao.delete(taxIdentifier)
            }
        }
    }
}
