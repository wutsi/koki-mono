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
    val listing: ListingEntity = ListingEntity(),

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "city_fk")
    var cityId: Long? = null,

    val deviceId: String? = null,
    var firstName: String = "",
    var lastName: String = "",
    val email: String = "",
    var phoneNumber: String = "",
    var message: String? = null,
    var visitRequestedAt: Date? = null,
    var status: LeadStatus = LeadStatus.UNKNOWN,
    val source: LeadSource = LeadSource.UNKNOWN,
    var nextContactAt: Date? = null,
    var nextVisitAt: Date? = null,
    var country: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
