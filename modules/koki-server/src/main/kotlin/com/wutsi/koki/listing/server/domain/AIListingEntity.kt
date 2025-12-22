package com.wutsi.koki.listing.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_AI_LISTING")
data class AIListingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,

    @OneToOne
    @JoinColumn(name = "listing_fk")
    val listing: ListingEntity = ListingEntity(),

    val text: String = "",
    val result: String = "",
    val createdAt: Date = Date(),
)
