package com.wutsi.koki.tenant.server.domain

import com.wutsi.koki.tenant.dto.TenantStatus
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TENANT")
data class TenantEntity(
    @Id
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "owner_fk")
    val owner: UserEntity = UserEntity(),

    val name: String = "",
    val domainName: String = "",
    val locale: String = "",
    val currency: String = "",
    val status: TenantStatus = TenantStatus.ACTIVE,
    val createdAt: Date = Date(),
)
