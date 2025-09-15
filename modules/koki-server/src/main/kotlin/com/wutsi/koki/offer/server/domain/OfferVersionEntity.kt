package com.wutsi.koki.offer.server.domain

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_NOTE")
data class OfferEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "buyer_contact_fk")
    val buyerContactId: Long = -1,

    @Column(name = "buyer_agent_user_fk")
    val buyerAgentUserId: Long? = null,

    val ownerId: Long? = null,
    val ownerType: ObjectType = ObjectType.UNKNOWN,
    val versionId: Long? = null,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val price: Long = 0,
    val currency: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
