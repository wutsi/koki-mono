package com.wutsi.koki.account.server.domain

import com.wutsi.koki.tenant.dto.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACCOUNT_USER")
data class AccountUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "account_fk")
    val accountId: Long = -1,

    var username: String = "",
    var password: String = "",
    val salt: String = "",
    var status: UserStatus = UserStatus.NEW,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
