package com.wutsi.koki.employee.dto

import jakarta.validation.constraints.Size
import java.util.Date

data class CreateEmployeeRequest(
    val userId: Long = -1,

    @get:Size(max = 100) val jobTitle: String? = null,
    @get:Size(min = 3, max = 3) val currency: String? = null,

    val hourlyWage: Double? = null,
    val status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    val hiredAt: Date? = null,
    val terminatedAt: Date? = null,
)
