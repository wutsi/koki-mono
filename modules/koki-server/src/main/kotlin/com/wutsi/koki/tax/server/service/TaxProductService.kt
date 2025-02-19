package com.wutsi.koki.tax.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.price.server.service.PriceService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
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
    private val securityService: SecurityService,
    private val priceService: PriceService,
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
    fun create(request: CreateTaxProductRequest, tenantId: Long): TaxProductEntity {
        val price = priceService.get(request.unitPriceId, tenantId)
        if (price.productId != request.productId) {
            throw IllegalStateException("Price and Product do not match")
        }
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        return dao.save(
            TaxProductEntity(
                tenantId = tenantId,
                taxId = request.taxId,
                productId = request.productId,
                unitPriceId = request.unitPriceId,
                unitPrice = price.amount,
                currency = price.currency,
                quantity = request.quantity,
                description = request.description,
                modifiedAt = now,
                createdAt = now,
                modifiedById = userId,
                createdById = userId
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateTaxProductRequest, tenantId: Long) {
        val taxProduct = get(id, tenantId)

        val price = priceService.get(request.unitPriceId, tenantId)
        if (price.productId != taxProduct.productId) {
            throw IllegalStateException("Price and Product do not match")
        }

        taxProduct.quantity = request.quantity
        taxProduct.unitPriceId = request.unitPriceId
        taxProduct.unitPrice = price.amount
        taxProduct.currency = price.currency
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
}
