package com.wutsi.koki.employee.dto

data class SearchEmployeeResponse(
    val employees: List<EmployeeSummary> = emptyList()
)
