package com.wutsi.koki.employee.server.domain

import com.wutsi.koki.employee.dto.EmployeeStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_EMPLOYEE")
data class EmployeeEntity(
    @Id
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "employee_type_fk")
    var employeeTypeId: Long? = null,

    var jobTitle: String? = null,
    var hourlyWage: Double? = null,
    var currency: String? = null,
    var status: EmployeeStatus = EmployeeStatus.UNKNOWN,
    var hiredAt: Date? = null,
    var terminatedAt: Date? = null,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
