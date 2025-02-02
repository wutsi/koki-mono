package com.wutsi.koki.employee.dto

import java.util.Date

data class Employee(
    val userId: Long = -1,
    val employeeTypeId: Long? = null,
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val jobTitle: String? = null,
    val hourlyWage: Double? = null,
    val currency: String? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val hiredAt: Date? = null,
    val terminatedAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
