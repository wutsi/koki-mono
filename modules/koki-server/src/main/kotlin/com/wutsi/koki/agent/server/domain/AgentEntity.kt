package com.wutsi.koki.agent.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_AGENT")
data class AgentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
