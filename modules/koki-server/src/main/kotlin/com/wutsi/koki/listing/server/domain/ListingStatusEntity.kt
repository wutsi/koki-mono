package com.wutsi.koki.listing.server.domain

import com.wutsi.koki.listing.dto.ListingStatus
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
@Table(name = "T_LISTING_STATUS")
data class ListingStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,
    @Column(name = "created_by_fk") val createdById: Long? = null,

    @ManyToOne
    @JoinColumn(name = "listing_fk")
    private val listing: ListingEntity = ListingEntity(),

    var status: ListingStatus = ListingStatus.UNKNOWN,
    val comment: String? = null,

    val createdAt: Date = Date(),
)
