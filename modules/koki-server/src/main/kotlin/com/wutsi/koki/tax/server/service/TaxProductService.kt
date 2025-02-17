package com.wutsi.koki.tax.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.Offer
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import com.wutsi.koki.tax.server.domain.TaxProductEntity
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TaxProductService(
    private val dao: TaxProductRepository,
    private val securityService: SecurityService
) {
    fun get(id: Long, tenantId: Long): TaxProductEntity {
        val tax = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.TAX_PRODUCT_NOT_FOUND)) }

        if (tax.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.TAX_PRODUCT_NOT_FOUND))
        }
        return tax
    }

    fun search(
        tenantId: Long,
        taxId: Long,
        limit: Int = 20,
        offset: Int = 0,
    ): List<TaxProductEntity> {
        val pageable = PageRequest.of(offset / limit, limit)
        return dao.findByTaxIdAndTenantId(taxId, tenantId, pageable)
    }

    @Transactional
    fun create(request: CreateTaxProductRequest, tenantId: Long): List<TaxProductEntity> {
        return request.offers.map { offer -> create(request.taxId, offer, tenantId) }
    }

    @Transactional
    fun update(id: Long, request: UpdateTaxProductRequest, tenantId: Long) {
        val taxProduct = get(id, tenantId)
        taxProduct.quantity = request.quantity
        taxProduct.unitPrice = request.unitPrice
        taxProduct.description = request.description
        taxProduct.modifiedById = securityService.getCurrentUserIdOrNull()
        taxProduct.modifiedAt = Date()
        dao.save(taxProduct)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val taxProduct = get(id, tenantId)
        dao.delete(taxProduct)
    }

    private fun create(taxId: Long, offer: Offer, tenantId: Long): TaxProductEntity {
        val taxProduct = dao.findByTaxIdAndProductId(taxId, offer.productId)
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()

        if (taxProduct != null && taxProduct.tenantId == tenantId) {
            taxProduct.quantity = taxProduct.quantity + 1
            taxProduct.unitPrice = offer.unitPrice
            taxProduct.modifiedById = securityService.getCurrentUserIdOrNull()
            taxProduct.modifiedAt = now
            return dao.save(taxProduct)
        } else {
            return dao.save(
                TaxProductEntity(
                    tenantId = tenantId,
                    taxId = taxId,
                    productId = offer.productId,
                    quantity = 1,
                    unitPrice = offer.unitPrice,
                    modifiedAt = now,
                    createdAt = now,
                    modifiedById = userId,
                    createdById = userId
                )
            )
        }
    }
}
