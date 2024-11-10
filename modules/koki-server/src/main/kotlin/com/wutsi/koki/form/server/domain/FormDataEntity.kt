package com.wutsi.koki.form.server.domain

import com.wutsi.koki.tenant.server.domain.TenantEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM")
data class FormEntity(
    @Id
    val id: String? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    var title: String = "",
    var active: Boolean = true,
    var content: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
