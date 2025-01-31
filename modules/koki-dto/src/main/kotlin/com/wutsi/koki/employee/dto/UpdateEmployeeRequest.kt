package com.wutsi.koki.employee.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateEmployeeRequest(
    @get:NotEmpty @get:Size(max = 100) var firstName: String = "",
    @get:NotEmpty @get:Size(max = 100) var lastName: String = "",
    @get:Size(max = 100) var jobTitle: String? = null,
    @get:Size(max = 3) var currency: String? = null,

    var hourlyWage: Double? = null,
    val status: EmployeeStatus = EmployeeStatus.NEW,
)
