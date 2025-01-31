package com.wutsi.koki.tax.server.domain

import com.wutsi.koki.tax.dto.TaxStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TAX")
data class TaxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "tax_type_fk")
    var taxTypeId: Long? = null,

    @Column(name = "account_fk")
    var accountId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @Column(name = "accountant_fk")
    var accountantId: Long? = null,

    @Column(name = "technician_fk")
    var technicianId: Long? = null,

    @Column(name = "assignee_fk")
    var assigneeId: Long? = null,

    var fiscalYear: Int = -1,
    var status: TaxStatus = TaxStatus.NEW,
    var description: String? = null,

    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var startAt: Date? = null,
    var dueAt: Date? = null,
)
