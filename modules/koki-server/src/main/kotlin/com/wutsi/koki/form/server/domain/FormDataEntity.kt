package com.wutsi.koki.form.server.domain

import com.wutsi.koki.tenant.server.domain.TenantEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM_DATA")
data class FormDataEntity(
    @Id
    val id: String? = null,

    @ManyToOne
    @JoinColumn(name = "tenant_fk")
    val tenant: TenantEntity = TenantEntity(),

    @ManyToOne
    @JoinColumn(name = "form_fk")
    val form: FormEntity = FormEntity(),

    var data: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
