package com.wutsi.koki.portal.employee.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class EmployeeModel(
    val user: UserModel = UserModel(),
    val employeeType: TypeModel? = null,
    val jobTitle: String? = null,
    val hourlyWage: MoneyModel? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val hiredAt: Date? = null,
    val hiredAtText: String? = null,
    val terminatedAt: Date? = null,
    val terminatedAtText: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val readOnly: Boolean = false,
) {
    val id: Long
        get() = user.id

    val name: String
        get() = user.displayName
}
