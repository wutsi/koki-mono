package com.wutsi.koki.employee.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateEmployeeRequest(
    @get:NotEmpty @get:Size(max = 100) val firstName: String = "",
    @get:NotEmpty @get:Size(max = 100) val lastName: String = "",
    @get:Size(max = 100) val jobTitle: String? = null,
    @get:Size(min = 3, max = 3) val currency: String? = null,

    val hourlyWage: Double? = null,
    val status: EmployeeStatus = EmployeeStatus.NEW,
)
