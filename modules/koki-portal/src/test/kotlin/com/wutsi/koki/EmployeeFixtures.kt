package com.wutsi.koki

import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.TenantFixtures.types
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.employee.dto.Employee
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.EmployeeSummary
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object EmployeeFixtures {
    val employees = listOf(
        EmployeeSummary(
            userId = users[0].id,
            employeeTypeId = types[0].id,
            jobTitle = "Director of Tech",
            status = EmployeeStatus.ACTIVE,
        ),
        EmployeeSummary(
            userId = users[1].id,
            employeeTypeId = types[1].id,
            jobTitle = "Accountant",
            status = EmployeeStatus.ACTIVE,
        ),
        EmployeeSummary(
            userId = users[2].id,
            employeeTypeId = types[1].id,
            jobTitle = null,
            status = EmployeeStatus.ACTIVE,
        ),
        EmployeeSummary(
            userId = users[3].id,
            employeeTypeId = types[2].id,
            jobTitle = null,
            status = EmployeeStatus.TERMINATED,
        ),
        EmployeeSummary(
            userId = users[4].id,
            jobTitle = null,
            status = EmployeeStatus.ACTIVE,
        ),
    )

    val employee = Employee(
        userId = users[0].id,
        employeeTypeId = types[2].id,
        jobTitle = "Accountant",
        status = EmployeeStatus.ACTIVE,
        hiredAt = DateUtils.addDays(Date(), -1500),
        terminatedAt = DateUtils.addDays(Date(), -1500),
        hourlyWage = 60.0,
        currency = tenants[0].currency,
    )
}
