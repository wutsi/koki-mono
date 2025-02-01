package com.wutsi.koki.portal.employee.form

import com.wutsi.koki.employee.dto.EmployeeStatus

data class CreateEmployeeForm(
    val email: String = "",
    val jobTitle: String? = null,
    val currency: String? = null,
    val hourlyWage: Double? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val hiredAt: String? = null,
    val terminatedAt: String? = null,
)
