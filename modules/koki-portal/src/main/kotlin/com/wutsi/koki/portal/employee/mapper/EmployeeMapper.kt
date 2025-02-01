package com.wutsi.koki.portal.employee.mapper

import com.wutsi.koki.employee.dto.Employee
import com.wutsi.koki.employee.dto.EmployeeSummary
import com.wutsi.koki.portal.employee.model.EmployeeModel
import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.model.MoneyMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class EmployeeMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toEmployeeModel(entity: Employee, users: Map<Long, UserModel>): EmployeeModel {
        val dateTimeFormat = createDateTimeFormat()
        val dateFormat = createDateFormat()

        return EmployeeModel(
            user = users[entity.userId] ?: UserModel(id = entity.userId),
            jobTitle = entity.jobTitle,
            hourlyWage = entity.hourlyWage?.let { amount -> moneyMapper.toMoneyModel(amount) },
            status = entity.status,
            hiredAt = entity.hiredAt,
            hiredAtText = entity.hiredAt?.let { date -> dateFormat.format(date) },
            terminatedAt = entity.terminatedAt,
            terminatedAtText = entity.terminatedAt?.let { date -> dateFormat.format(date) },
            modifiedAt = entity.modifiedAt,
            modifiedAtText = dateTimeFormat.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = dateTimeFormat.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }

    fun toEmployeeModel(entity: EmployeeSummary, users: Map<Long, UserModel>): EmployeeModel {
        val dateTimeFormat = createDateTimeFormat()

        return EmployeeModel(
            user = users[entity.userId] ?: UserModel(id = entity.userId),
            jobTitle = entity.jobTitle,
            status = entity.status,
            modifiedAt = entity.modifiedAt,
            modifiedAtText = dateTimeFormat.format(entity.modifiedAt),
            modifiedBy = entity.modifiedById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            createdAtText = dateTimeFormat.format(entity.createdAt),
            createdBy = entity.createdById?.let { id -> users[id] },
        )
    }
}
