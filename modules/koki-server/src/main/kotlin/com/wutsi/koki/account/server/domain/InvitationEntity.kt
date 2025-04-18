package com.wutsi.koki.account.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_INVITATION")
data class InvitationEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "account_fk")
    val accountId: Long = -1,

    @Column(name = "created_by_fk")
    val createById: Long? = null,

    val createdAt: Date = Date(),
)
