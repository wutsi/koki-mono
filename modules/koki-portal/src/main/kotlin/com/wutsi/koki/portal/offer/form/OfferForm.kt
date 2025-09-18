package com.wutsi.koki.portal.offer.form

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus

data class OfferForm(
    val id: Long = -1,
    val ownerId: Long? = null,
    val ownerType: ObjectType? = null,
    val price: Long? = null,
    val currency: String? = null,
    val pricePerMonth: Boolean = false,
    val contingencies: String? = null,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val expiresAt: String? = null,
    val closingAt: String? = null,
    val expiresAtMin: String? = null,
    val sellerAgentUserId: Long = -1,
    val buyerAgentUserId: Long = -1,
    val buyerContactId: Long = -1,

    val status: OfferStatus? = null,
    val comment: String? = null,
)
