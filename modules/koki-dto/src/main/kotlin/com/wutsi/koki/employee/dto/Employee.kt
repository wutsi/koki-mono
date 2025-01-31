package com.wutsi.koki.employee.dto

import java.util.Date

data class Employee(
    val id: Long = -1,
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val jobTitle: String? = null,
    val hourlyWage: Double? = null,
    val currency: String? = null,
    val status: EmployeeStatus = EmployeeStatus.NEW,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
