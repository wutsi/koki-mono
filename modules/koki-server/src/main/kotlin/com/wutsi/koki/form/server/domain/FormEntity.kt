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

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    var name: String = "",
    var title: String = "",
    var description: String? = null,
    var active: Boolean = true,
    var content: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deleted: Boolean = false,
    var deletedAt: Date? = null,
)
