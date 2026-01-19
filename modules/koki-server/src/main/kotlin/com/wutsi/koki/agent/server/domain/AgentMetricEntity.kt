package com.wutsi.koki.agent.server.domain

import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.listing.dto.ListingType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Deprecated("")
@Entity
@Table(name = "T_AGENT_METRIC")
data class AgentMetricEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "agent_fk")
    val agentId: Long = -1,

    val listingType: ListingType = ListingType.UNKNOWN,
    val period: MetricPeriod = MetricPeriod.UNKNOWN,
    var total: Long = 0,
    var minPrice: Long = 0L,
    var maxPrice: Long = 0L,
    var averagePrice: Long = 0L,
    var totalPrice: Long = 0L,
    var currency: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
