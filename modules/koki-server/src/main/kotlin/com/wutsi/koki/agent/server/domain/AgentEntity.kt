package com.wutsi.koki.agent.server.domain

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
@Table(name = "T_AGENT")
data class AgentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Deprecated("")
    var totalRentals: Long? = null,
    @Deprecated("")
    var totalSales: Long? = null,
    @Deprecated("")
    var totalTransactions: Long? = null,

    @Deprecated("")
    @Column(name = "past_12m_sales")
    var past12mSales: Long? = null,

    @Deprecated("")
    @Column(name = "past_12m_rentals")
    var past12mRentals: Long? = null,

    @Deprecated("")
    @Column(name = "past_12m_transactions")
    var past12mTransactions: Long? = null,

    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var lastSoldAt: Date? = null,

    @Deprecated("")
    @OneToMany()
    @JoinColumn("agent_fk")
    val metrics: List<AgentMetricEntity> = listOf(),
)
