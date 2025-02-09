package com.wutsi.koki.price.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.UpdatePriceRequest
import com.wutsi.koki.product.server.dao.PriceRepository
import com.wutsi.koki.product.server.domain.PriceEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PriceService(
    private val dao: PriceRepository,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): PriceEntity {
        val price = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.PRICE_NOT_FOUND)) }

        if (price.tenantId != tenantId || price.deleted) {
            throw NotFoundException(Error(ErrorCode.PRICE_NOT_FOUND))
        }
        return price
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        productIds: List<Long> = emptyList(),
        accountTypeIds: List<Long> = emptyList(),
        currency: String? = null,
        date: Date? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<PriceEntity> {
        val jql = StringBuilder("SELECT P FROM PriceEntity AS P WHERE P.deleted=false AND P.tenantId=:tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND P.id IN :ids")
        }
        if (productIds.isNotEmpty()) {
            jql.append(" AND P.productId IN :productIds")
        }
        if (accountTypeIds.isNotEmpty()) {
            jql.append(" AND P.accountTypeId IN :accountTypeIds")
        }
        if (currency != null) {
            jql.append(" AND P.currency IN :currency")
        }
        if (active != null) {
            jql.append(" AND P.active = :active")
        }
        if (date != null) {
            jql.append(" AND (P.startAt IS NULL OR P.startAt <= :date) AND (P.endAt IS NULL OR P.endAt >= :date)")
        }

        val query = em.createQuery(jql.toString(), PriceEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (productIds.isNotEmpty()) {
            query.setParameter("productIds", productIds)
        }
        if (accountTypeIds.isNotEmpty()) {
            query.setParameter("accountTypeIds", accountTypeIds)
        }
        if (currency != null) {
            query.setParameter("currency", currency)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        if (date != null) {
            query.setParameter("date", date)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreatePriceRequest, tenantId: Long): PriceEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        return dao.save(
            PriceEntity(
                tenantId = tenantId,
                productId = request.productId,
                accountTypeId = request.accountTypeId,
                name = request.name,
                amount = request.amount,
                currency = request.currency,
                startAt = request.startAt,
                endAt = request.endAt,
                active = request.active,
                createdAt = now,
                createdById = userId,
                modifiedAt = now,
                modifiedById = userId,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdatePriceRequest, tenantId: Long) {
        val price = get(id, tenantId)
        price.accountTypeId = request.accountTypeId
        price.name = request.name
        price.active = request.active
        price.amount = request.amount
        price.currency = request.currency
        price.startAt = request.startAt
        price.endAt = request.endAt
        price.modifiedAt = Date()
        price.modifiedById = securityService.getCurrentUserIdOrNull()
        dao.save(price)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val price = get(id, tenantId)
        price.deleted = true
        price.deletedAt = Date()
        price.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(price)
    }
}
