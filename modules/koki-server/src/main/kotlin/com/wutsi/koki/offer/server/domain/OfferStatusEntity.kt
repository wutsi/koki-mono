package com.wutsi.koki.offer.server.domain

import com.wutsi.koki.offer.dto.OfferStatus
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
@Table(name = "T_OFFER_STATUS")
data class OfferStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @OneToOne()
    @JoinColumn(name = "offer_fk")
    val offer: OfferEntity? = null,

    @OneToOne()
    @JoinColumn(name = "version_fk")
    val version: OfferVersionEntity? = null,

    val status: OfferStatus = OfferStatus.UNKNOWN,
    val comment: String? = null,
    val createdAt: Date = Date(),
)
