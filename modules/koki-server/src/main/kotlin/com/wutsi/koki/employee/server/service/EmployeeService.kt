package com.wutsi.koki.employee.server.service

import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import com.wutsi.koki.employee.server.domain.EmployeeEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class EmployeeService(
    private val dao: EmployeeRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
) {
    fun get(id: Long, tenantId: Long): EmployeeEntity {
        val employee = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.EMPLOYEE_NOT_FOUND)) }

        if (employee.tenantId != tenantId || employee.deleted) {
            throw NotFoundException(Error(ErrorCode.EMPLOYEE_NOT_FOUND))
        }
        return employee
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        statuses: List<EmployeeStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<EmployeeEntity> {
        val jql = StringBuilder("SELECT E FROM EmployeeEntity E WHERE E.deleted=false AND E.tenantId = :tenantId")

        if (keyword != null) {
            jql.append(" AND ( (UPPER(E.firstName) LIKE :keyword) OR (UPPER(E.lastName) LIKE :keyword) )")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND E.id IN :ids")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND E.status IN :statuses")
        }
        jql.append(" ORDER BY E.firstName, E.lastName")

        val query = em.createQuery(jql.toString(), EmployeeEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateEmployeeRequest, tenantId: Long): EmployeeEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        return dao.save(
            EmployeeEntity(
                tenantId = tenantId,
                firstName = request.firstName,
                lastName = request.lastName,
                jobTitle = request.jobTitle,
                hourlyWage = request.hourlyWage,
                currency = request.currency,
                status = request.status,
                createdById = userId,
                modifiedById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateEmployeeRequest, tenantId: Long) {
        val employee = get(id, tenantId)
        employee.firstName = request.firstName
        employee.lastName = request.lastName
        employee.jobTitle = request.jobTitle
        employee.status = request.status
        employee.hourlyWage = request.hourlyWage
        employee.currency = request.currency
        employee.modifiedAt = Date()
        employee.modifiedById = securityService.getCurrentUserIdOrNull()
        dao.save(employee)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val employee = get(id, tenantId)
        employee.deleted = true
        employee.deletedAt = Date()
        employee.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(employee)
    }
}
