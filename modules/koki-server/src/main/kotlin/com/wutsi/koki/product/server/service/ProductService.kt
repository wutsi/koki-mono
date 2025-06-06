package com.wutsi.koki.product.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.price.server.service.PriceService
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.UpdateProductRequest
import com.wutsi.koki.product.server.dao.ProductRepository
import com.wutsi.koki.product.server.domain.ProductEntity
import com.wutsi.koki.product.server.domain.ServiceDetailsData
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ProductService(
    private val dao: ProductRepository,
    private val securityService: SecurityService,
    private val priceService: PriceService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): ProductEntity {
        val product = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.PRODUCT_NOT_FOUND)) }

        if (product.tenantId != tenantId || product.deleted) {
            throw NotFoundException(Error(ErrorCode.PRODUCT_NOT_FOUND))
        }
        return product
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        types: List<ProductType> = emptyList(),
        keyword: String? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ProductEntity> {
        val jql = StringBuilder("SELECT P FROM ProductEntity AS P WHERE P.deleted=false AND P.tenantId=:tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND P.id IN :ids")
        }
        if (types.isNotEmpty()) {
            jql.append(" AND P.type IN :types")
        }
        if (keyword != null) {
            jql.append(" AND ( (UPPER(P.code) LIKE :keyword) OR (UPPER(P.name) LIKE :keyword) )")
        }
        if (active != null) {
            jql.append(" AND P.active = :active")
        }
        jql.append(" ORDER BY P.name")

        val query = em.createQuery(jql.toString(), ProductEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (types.isNotEmpty()) {
            query.setParameter("types", types)
        }
        if (keyword != null) {
            query.setParameter("keyword", "${keyword.uppercase()}%")
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateProductRequest, tenantId: Long): ProductEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val product = dao.save(
            ProductEntity(
                tenantId = tenantId,
                name = request.name,
                code = request.code,
                active = request.active,
                description = request.description,
                type = request.type,
                createdAt = now,
                createdById = userId,
                modifiedAt = now,
                modifiedById = userId,
                categoryId = request.categoryId,
                serviceDetails = ServiceDetailsData(
                    unitId = request.unitId,
                    quantity = request.quantity
                )
            )
        )
        if (request.unitPrice != null) {
            if (request.currency.isEmpty()) {
                throw BadRequestException(
                    error = Error(ErrorCode.PRICE_CURRENCY_MISSING)
                )
            }

            priceService.create(
                request = CreatePriceRequest(
                    productId = product.id!!,
                    amount = request.unitPrice!!,
                    currency = request.currency,
                    active = true,
                ),
                tenantId = tenantId
            )
        }
        return product
    }

    @Transactional
    fun update(id: Long, request: UpdateProductRequest, tenantId: Long) {
        val product = get(id, tenantId)
        product.code = request.code
        product.name = request.name
        product.active = request.active
        product.description = request.description
        product.type = request.type
        product.modifiedAt = Date()
        product.modifiedById = securityService.getCurrentUserIdOrNull()
        product.categoryId = request.categoryId
        product.serviceDetails = ServiceDetailsData(
            unitId = request.unitId,
            quantity = request.quantity
        )
        dao.save(product)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val product = get(id, tenantId)
        product.deleted = true
        product.deletedAt = Date()
        product.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(product)
    }
}
