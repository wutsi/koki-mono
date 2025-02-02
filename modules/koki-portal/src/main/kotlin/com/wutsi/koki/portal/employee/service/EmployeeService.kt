package com.wutsi.koki.portal.employee.service

import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.portal.employee.form.CreateEmployeeForm
import com.wutsi.koki.portal.employee.form.UpdateEmployeeForm
import com.wutsi.koki.portal.employee.mapper.EmployeeMapper
import com.wutsi.koki.portal.employee.model.EmployeeModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiEmployees
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class EmployeeService(
    private val koki: KokiEmployees,
    private val mapper: EmployeeMapper,
    private val userService: UserService,
    private val typeService: TypeService,
) {
    fun employee(
        id: Long,
        fullGraph: Boolean = true,
    ): EmployeeModel {
        val employee = koki.employee(id).employee

        val userIds = listOf(employee.userId, employee.createdById, employee.modifiedById)
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val employeeType = if (employee.employeeTypeId != null) {
            if (fullGraph) {
                typeService.type(employee.employeeTypeId!!)
            } else {
                TypeModel(id = employee.employeeTypeId!!)
            }
        } else {
            null
        }

        return mapper.toEmployeeModel(employee, users, employeeType)
    }

    fun employees(
        ids: List<Long> = emptyList(),
        statuses: List<EmployeeStatus> = emptyList(),
        employeeTypeIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<EmployeeModel> {
        val employees = koki.employees(
            ids = ids,
            statuses = statuses,
            employeeTypeIds = employeeTypeIds,
            limit = limit,
            offset = offset
        ).employees

        val userIds = employees.flatMap { employee ->
            listOf(employee.userId, employee.createdById, employee.modifiedById)
        }
            .filterNotNull()
            .toSet()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val employeeTypeIds = employees.map { employee -> employee.employeeTypeId }
            .filterNotNull()
            .toSet()
        val employeeTypes = if (employeeTypeIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            typeService.types(
                ids = employeeTypeIds.toList(),
                limit = employeeTypeIds.size,
            ).associateBy { type -> type.id }
        }

        return employees.map { employee -> mapper.toEmployeeModel(employee, users, employeeTypes) }
    }

    fun create(form: CreateEmployeeForm): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return koki.create(
            CreateEmployeeRequest(
                email = form.email,
                employeeTypeId = form.employeeTypeId,
                hourlyWage = form.hourlyWage,
                currency = form.currency,
                jobTitle = form.jobTitle?.ifEmpty { null },
                hiredAt = form.hiredAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                terminatedAt = form.terminatedAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                status = form.status,
            )
        ).employeeId
    }

    fun update(id: Long, form: UpdateEmployeeForm) {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        koki.update(
            id,
            UpdateEmployeeRequest(
                employeeTypeId = form.employeeTypeId,
                hourlyWage = form.hourlyWage,
                currency = form.currency,
                jobTitle = form.jobTitle?.ifEmpty { null },
                hiredAt = form.hiredAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                terminatedAt = form.terminatedAt?.ifEmpty { null }?.let { date -> fmt.parse(date) },
                status = form.status,
            )
        )
    }
}
