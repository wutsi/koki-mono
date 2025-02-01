package com.wutsi.koki.portal.employee

data class CreateEmployeeForm(
    val email: String = "",
    val jobTitle: String? = null,
    val currency: String? = null,
    val hourlyWage: Double? = null,
    val hiredAt: String? = null,
)
