package com.wutsi.koki.employee.dto

import jakarta.validation.constraints.Size
import java.util.Date

data class UpdateEmployeeRequest(
    @get:Size(max = 100) var jobTitle: String? = null,
    @get:Size(max = 3) var currency: String? = null,

    val employeeTypeId: Long? = null,
    var hourlyWage: Double? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val hiredAt: Date? = null,
    val terminatedAt: Date? = null,
)
