package com.wutsi.koki.lead.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_LEAD_MESSAGE")
data class LeadMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @ManyToOne()
    @JoinColumn(name = "lead_fk")
    val lead: LeadEntity = LeadEntity(),

    var content: String = "",
    var visitRequestedAt: Date? = null,
    val createdAt: Date = Date(),
)
