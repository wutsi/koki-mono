package com.wutsi.koki.file.server.domain

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
@Table(name = "T_FILE")
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @OneToMany()
    @JoinColumn(name = "file_fk")
    val fileOwners: List<FileOwnerEntity> = emptyList(),

    var workflowInstanceId: String? = null,
    val formId: String? = null,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var deletedAt: Date? = null,
)
