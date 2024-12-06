package com.wutsi.koki.file.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FILE")
data class FileEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    var workflowInstanceId: String? = null,
    val formId: String? = null,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
