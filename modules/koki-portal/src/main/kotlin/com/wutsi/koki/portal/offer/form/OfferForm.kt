package com.wutsi.koki.portal.offer.form

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferParty

data class OfferForm(
    val id: Long = -1,
    val ownerId: Long? = null,
    val ownerType: ObjectType? = null,
    val price: Long? = null,
    val pricePerMonth: Boolean = false,
    val contingencies: String? = null,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val expiresAt: String? = null,
    val expiresAtMin: String? = null,
    val sellerAgentUserId: Long? = null,
    val buyerAgentUserId: Long? = null,
    val buyerContactId: Long? = null
)
