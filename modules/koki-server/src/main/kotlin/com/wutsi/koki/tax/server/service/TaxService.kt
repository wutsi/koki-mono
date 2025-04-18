package com.wutsi.koki.tax.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.employee.server.service.EmployeeService
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.note.server.service.NoteService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxAssigneeRequest
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import com.wutsi.koki.tax.server.dao.TaxRepository
import com.wutsi.koki.tax.server.domain.TaxEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TaxService(
    private val dao: TaxRepository,
    private val taxProductDao: TaxProductRepository,
    private val noteService: NoteService,
    private val employeeService: EmployeeService,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): TaxEntity {
        val tax = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.TAX_NOT_FOUND)) }

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
        assigneeIds: List<Long> = emptyList(),
        participantIds: List<Long> = emptyList(),
        createdByIds: List<Long> = emptyList(),
        statuses: List<TaxStatus> = emptyList(),
        fiscalYear: Int? = null,
        startAtFrom: Date? = null,
        startAtTo: Date? = null,
        dueAtFrom: Date? = null,
        dueAtTo: Date? = null,
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
        if (assigneeIds.isNotEmpty()) {
            jql.append(" AND T.assigneeId IN :assigneeIds")
        }
        if (participantIds.isNotEmpty()) {
            jql.append(" AND (T.assigneeId IN :participantIds OR T.accountantId IN :participantIds OR T.technicianId IN :participantIds)")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND T.status IN :statuses")
        }
        if (fiscalYear != null) {
            jql.append(" AND T.fiscalYear IN :fiscalYear")
        }
        if (startAtFrom != null) {
            jql.append(" AND T.startAt >= :startAtFrom")
        }
        if (startAtTo != null) {
            jql.append(" AND T.startAt <= :startAtTo")
        }
        if (dueAtFrom != null) {
            jql.append(" AND T.dueAt >= :dueAtFrom")
        }
        if (dueAtTo != null) {
            jql.append(" AND T.dueAt <= :dueAtTo")
        }
        jql.append(" ORDER BY T.fiscalYear DESC, T.startAt")

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
        if (assigneeIds.isNotEmpty()) {
            query.setParameter("assigneeIds", assigneeIds)
        }
        if (participantIds.isNotEmpty()) {
            query.setParameter("participantIds", participantIds)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (fiscalYear != null) {
            query.setParameter("fiscalYear", fiscalYear)
        }
        if (startAtFrom != null) {
            query.setParameter("startAtFrom", startAtFrom)
        }
        if (startAtTo != null) {
            query.setParameter("startAtTo", startAtTo)
        }
        if (dueAtFrom != null) {
            query.setParameter("dueAtFrom", dueAtFrom)
        }
        if (dueAtTo != null) {
            query.setParameter("dueAtTo", dueAtTo)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateTaxRequest, tenantId: Long): TaxEntity {
        val userId = securityService.getCurrentUserId()
        return dao.save(
            TaxEntity(
                tenantId = tenantId,
                taxTypeId = request.taxTypeId,
                accountId = request.accountId,
                accountantId = request.accountantId,
                technicianId = request.technicianId,
                assigneeId = request.assigneeId,
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
        tax.accountId = request.accountId
        tax.accountantId = request.accountantId
        tax.technicianId = request.technicianId
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
    fun status(tax: TaxEntity, request: UpdateTaxStatusRequest): TaxEntity {
        val now = Date()
        tax.status = request.status
        tax.modifiedById = securityService.getCurrentUserId()
        tax.modifiedAt = now
        if (request.status.ordinal > TaxStatus.NEW.ordinal && tax.startAt == null) {
            tax.startAt = now
        }
        return dao.save(tax)
    }

    @Transactional
    fun assignee(tax: TaxEntity, request: UpdateTaxAssigneeRequest): TaxEntity {
        val now = Date()
        tax.assigneeId = request.assigneeId
        tax.modifiedById = securityService.getCurrentUserId()
        tax.modifiedAt = now
        return dao.save(tax)
    }

    @Transactional
    fun updateMetrics(id: Long, tenantId: Long) {
        val tax = get(id, tenantId)
        val taxProducts = taxProductDao.findByTaxId(tax.id!!)

        // Number of products
        tax.productCount = taxProducts.size

        // Revenu
        tax.totalRevenue = taxProducts.sumOf { taxProduct -> taxProduct.subTotal }
        tax.currency = taxProducts.firstOrNull()?.currency

        // Labor duration
        val notes = noteService.search(
            tenantId = tax.tenantId,
            ownerId = tax.id,
            ownerType = ObjectType.TAX,
        )
        tax.totalLaborDuration = notes.sumOf { note -> note.duration }

        // Labor cost
        val userIds = notes.map { note -> note.createdById }.filterNotNull().distinct()
        val notesByUserId = notes.groupBy { note -> note.createdById }
        val employees = employeeService.search(
            tenantId = tax.tenantId,
            ids = userIds,
            limit = userIds.size
        ).associateBy { employee -> employee.id }

        var totalLaborCost = 0.0
        notesByUserId.forEach { entry ->
            val employee = employees[entry.key]
            if (employee != null) {
                val userDuration = entry.value.sumOf { note -> note.duration } // Duration in minutes
                employee.hourlyWage?.let { wage ->
                    totalLaborCost += wage * userDuration.toDouble() / 60.0
                }
            }
        }
        tax.totalLaborCost = totalLaborCost
        dao.save(tax)
    }
}
