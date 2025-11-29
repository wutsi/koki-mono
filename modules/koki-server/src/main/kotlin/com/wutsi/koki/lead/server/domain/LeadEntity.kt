package com.wutsi.koki.lead.server.domain

import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
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
@Table(name = "T_LEAD")
data class LeadEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @ManyToOne()
    @JoinColumn(name = "listing_fk")
    val listing: ListingEntity? = null,

    @Column(name = "user_fk")
    val userId: Long? = null,

    val deviceId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String? = null,
    val phoneNumber: String = "",
    val message: String? = null,
    val visitRequestedAt: Date? = null,
    var status: LeadStatus = LeadStatus.UNKNOWN,
    val source: LeadSource = LeadSource.UNKNOWN,
    var nextContactAt: Date? = null,
    var nextVisitAt: Date? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
