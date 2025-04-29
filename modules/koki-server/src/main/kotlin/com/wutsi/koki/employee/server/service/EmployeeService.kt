package com.wutsi.koki.employee.server.service

import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import com.wutsi.koki.employee.server.domain.EmployeeEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class EmployeeService(
    private val dao: EmployeeRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
    private val userService: UserService,
) {
    fun get(id: Long, tenantId: Long): EmployeeEntity {
        val employee = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.EMPLOYEE_NOT_FOUND)) }

        if (employee.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.EMPLOYEE_NOT_FOUND))
        }
        return employee
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        statuses: List<EmployeeStatus> = emptyList(),
        employeeTypeIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<EmployeeEntity> {
        val jql = StringBuilder("SELECT E FROM EmployeeEntity E WHERE E.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND E.id IN :ids")
        }
        if (statuses.isNotEmpty()) {
            jql.append(" AND E.status IN :statuses")
        }
        if (employeeTypeIds.isNotEmpty()) {
            jql.append(" AND E.employeeTypeId IN :employeeTypeIds")
        }

        val query = em.createQuery(jql.toString(), EmployeeEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (statuses.isNotEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (employeeTypeIds.isNotEmpty()) {
            query.setParameter("employeeTypeIds", employeeTypeIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateEmployeeRequest, tenantId: Long): EmployeeEntity {
        val user = userService.getByEmail(request.email, UserType.EMPLOYEE, tenantId)
        val employee = user.id?.let { id -> dao.findById(id).getOrNull() }
        if (employee != null) {
            throw ConflictException(
                error = Error(ErrorCode.EMPLOYEE_ALREADY_EXIST)
            )
        }

        val currentUserId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        return dao.save(
            EmployeeEntity(
                id = user.id,
                tenantId = tenantId,
                employeeTypeId = request.employeeTypeId,
                jobTitle = request.jobTitle,
                hourlyWage = request.hourlyWage,
                currency = request.currency,
                status = request.status,
                hiredAt = request.hiredAt,
                terminatedAt = request.terminatedAt,
                createdById = currentUserId,
                modifiedById = currentUserId,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateEmployeeRequest, tenantId: Long) {
        val employee = get(id, tenantId)
        employee.employeeTypeId = request.employeeTypeId
        employee.jobTitle = request.jobTitle
        employee.status = request.status
        employee.hourlyWage = request.hourlyWage
        employee.currency = request.currency
        employee.hiredAt = request.hiredAt
        employee.terminatedAt = request.terminatedAt
        employee.modifiedAt = Date()
        employee.modifiedById = securityService.getCurrentUserIdOrNull()
        dao.save(employee)
    }
}
