package com.wutsi.koki.portal.offer.model

import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.common.model.MoneyModel
import java.util.Date

data class OfferVersionModel(
    val id: Long = -1,
    val offerId: Long = -1,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val assigneeUserId: Long? = null,
    val price: MoneyModel = MoneyModel(),
    val priceDiff: MoneyModel? = null,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val contingencies: String? = null,
    val createdAt: Date = Date(),
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
    val createdAtText: String = "",
    val expiresAtText: String? = null,
    val closingAtText: String? = null,
) {
    val contingenciesHtml: String?
        get() = contingencies?.let { HtmlUtils.toHtml(contingencies) }

    val submittedBySeller: Boolean
        get() = submittingParty == OfferParty.SELLER

    val submittedByBuyer: Boolean
        get() = submittingParty == OfferParty.BUYER
}
