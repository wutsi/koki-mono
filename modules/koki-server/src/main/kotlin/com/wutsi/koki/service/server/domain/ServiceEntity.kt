package com.wutsi.koki.service.server.domain

import com.wutsi.koki.service.dto.AuthorizationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_SERVICE")
data class ServiceEntity(
    @Id
    val id: String? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    var name: String = "",
    var title: String? = null,
    var description: String? = null,
    var baseUrl: String = "",
    var authorizationType: AuthorizationType = AuthorizationType.UNKNOWN,
    var username: String? = null,
    var password: String? = null,
    var apiKey: String? = null,
    var active: Boolean = true,
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
