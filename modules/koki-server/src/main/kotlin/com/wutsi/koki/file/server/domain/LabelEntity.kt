package com.wutsi.koki.file.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_LABEL")
data class LabelEntity(
    @Id
    val id: Long? = null,

    @Column("tenant_fk")
    val tenantId: Long = -1,

    val name: String = "",
    val displayName: String = "",
    val createdAt: Date = Date()
)
