package com.wutsi.koki.offer.server.domain

import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
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
@Table(name = "T_OFFER_VERSION")
data class OfferVersionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "assignee_user_fk")
    val assigneeUserId: Long? = null,

    @ManyToOne()
    @JoinColumn(name = "offer_fk")
    val offer: OfferEntity = OfferEntity(),

    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: Long = 0,
    val currency: String = "",
    var status: OfferStatus = OfferStatus.UNKNOWN,
    val contingencies: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
