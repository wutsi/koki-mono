package com.wutsi.koki.account.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACCOUNT_INVITATION")
data class AccountInvitationEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "account_fk")
    val accountId: Long = -1,

    @Column(name = "created_by_fk")
    val createById: Long = -1,

    val createdAt: Date = Date(),
)
