package com.wutsi.koki.tax.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import com.wutsi.koki.tax.server.dao.TaxRepository
import com.wutsi.koki.tax.server.domain.TaxEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TaxService(
    private val dao: TaxRepository,
    private val securityService: SecurityService,
    private val accountService: AccountService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): TaxEntity {
        val tax = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.TAX_NOT_FOUND)) }

        if (tax.tenantId != tenantId || tax.deleted) {
            throw NotFoundException(Error(ErrorCode.TAX_NOT_FOUND))
        }
        return tax
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        taxTypeIds: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        accountantIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        statuses: List<TaxStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): List<TaxEntity> {
        val jql = StringBuilder("SELECT T FROM TaxEntity T WHERE T.deleted=false AND T.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND T.id IN :ids")
        }
        if (taxTypeIds.isNotEmpty()) {
            jql.append(" AND T.taxTypeId IN :taxTypeIds")
        }
        if (createdByIds.isNotEmpty()) {
            jql.append(" AND T.createdById IN :createdByIds")
        }
        if (accountIds.isNotEmpty()) {
            jql.append(" AND T.accountId IN :accountIds")
        }
        if (accountantIds.isNotEmpty()) {
            jql.append(" AND T.accountantId IN :accountantIds")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND T.status IN :statuses")
        }
        jql.append(" ORDER BY T.fiscalYear DESC, T.id DESC")

        val query = em.createQuery(jql.toString(), TaxEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (taxTypeIds.isNotEmpty()) {
            query.setParameter("taxTypeIds", taxTypeIds)
        }
        if (createdByIds.isNotEmpty()) {
            query.setParameter("createdByIds", createdByIds)
        }
        if (accountIds.isNotEmpty()) {
            query.setParameter("accountIds", accountIds)
        }
        if (accountantIds.isNotEmpty()) {
            query.setParameter("accountantIds", accountantIds)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateTaxRequest, tenantId: Long): TaxEntity {
        val userId = securityService.getCurrentUserId()
        val accountantId = request.accountantId
            ?: accountService.get(request.accountId, tenantId).managedById
        return dao.save(
            TaxEntity(
                tenantId = tenantId,
                taxTypeId = request.taxTypeId,
                accountId = request.accountId,
                accountantId = accountantId,
                status = TaxStatus.NEW,
                fiscalYear = request.fiscalYear,
                description = request.description,
                createdById = userId,
                modifiedById = userId,
                startAt = request.startAt,
                dueAt = request.dueAt,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateTaxRequest, tenantId: Long) {
        val tax = get(id, tenantId)
        tax.dueAt = request.dueAt
        tax.description = request.description
        tax.accountantId = request.accountantId
        tax.fiscalYear = request.fiscalYear
        tax.startAt = request.startAt
        tax.taxTypeId = request.taxTypeId
        tax.modifiedById = securityService.getCurrentUserId()
        dao.save(tax)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val tax = get(id, tenantId)
        tax.deleted = true
        tax.deletedAt = Date()
        tax.deletedById = securityService.getCurrentUserId()
        dao.save(tax)
    }

    @Transactional
    fun status(id: Long, request: UpdateTaxStatusRequest, tenantId: Long) {
        val tax = get(id, tenantId)
        tax.status = request.status
        tax.modifiedById = securityService.getCurrentUserId()
        tax.modifiedAt = Date()
        dao.save(tax)
    }
}
