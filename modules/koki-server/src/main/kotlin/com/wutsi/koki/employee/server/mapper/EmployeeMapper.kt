package com.wutsi.koki.employee.server.mapper

import com.wutsi.koki.employee.dto.Employee
import com.wutsi.koki.employee.dto.EmployeeSummary
import com.wutsi.koki.employee.server.domain.EmployeeEntity
import org.springframework.stereotype.Service

@Service
class EmployeeMapper {
    fun toEmployee(entity: EmployeeEntity): Employee {
        return Employee(
            id = entity.id!!,
            firstName = entity.firstName,
            lastName = entity.lastName,
            status = entity.status,
            jobTitle = entity.jobTitle,
            hourlyWage = entity.hourlyWage,
            currency = entity.currency,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            createdAt = entity.createdAt,
        )
    }

    fun toEmployeeSummary(entity: EmployeeEntity): EmployeeSummary {
        return EmployeeSummary(
            id = entity.id!!,
            firstName = entity.firstName,
            lastName = entity.lastName,
            status = entity.status,
            jobTitle = entity.jobTitle,
            hourlyWage = entity.hourlyWage,
            currency = entity.currency,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.modifiedById,
            createdAt = entity.createdAt,
        )
    }
}
