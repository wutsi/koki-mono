package com.wutsi.koki.employee.dto

import java.util.Date

data class EmployeeSummary(
    val userId: Long = -1,
    val employeeTypeId: Long? = null,
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val jobTitle: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
