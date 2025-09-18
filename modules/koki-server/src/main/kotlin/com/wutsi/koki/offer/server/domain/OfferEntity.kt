package com.wutsi.koki.offer.server.domain

import com.wutsi.koki.common.dto.ObjectType
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
@Table(name = "T_OFFER")
data class OfferEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "buyer_contact_fk")
    val buyerContactId: Long = -1,

    @Column(name = "buyer_agent_user_fk")
    val buyerAgentUserId: Long = -1,

    @Column(name = "seller_agent_user_fk")
    val sellerAgentUserId: Long = -1,

    @Column(name = "owner_fk")
    val ownerId: Long? = null,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    val ownerType: ObjectType? = null,

    @OneToOne()
    @JoinColumn(name = "version_fk")
    var version: OfferVersionEntity? = null,

    var status: OfferStatus = OfferStatus.UNKNOWN,
    var totalVersions: Int = 0,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
