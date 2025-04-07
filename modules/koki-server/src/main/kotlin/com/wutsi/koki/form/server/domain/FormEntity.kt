package com.wutsi.koki.form.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FORM")
data class FormEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    var code: String = "",
    var name: String = "",
    var description: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deleted: Boolean = false,
    var deletedAt: Date? = null,

    @OneToMany()
    @JoinColumn(name = "form_fk")
    val formOwners: List<FormOwnerEntity> = emptyList(),
)
