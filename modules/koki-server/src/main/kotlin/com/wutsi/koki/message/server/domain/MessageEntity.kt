package com.wutsi.koki.form.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM")
data class FormEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var name: String = "",
    var title: String = "",
    var active: Boolean = true,
    var content: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
