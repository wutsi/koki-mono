package com.wutsi.koki.tax.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TAX_FILE")
data class TaxFileEntity(
    @Id
    @Column("file_fk")
    val id: Long? = null,

    @Column(name = "tax_fk")
    val taxId: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var data: String = "",

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
