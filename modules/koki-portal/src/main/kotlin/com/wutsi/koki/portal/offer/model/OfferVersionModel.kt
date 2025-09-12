package com.wutsi.koki.portal.offer.page.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.platform.util.HtmlUtils
import java.util.Date

data class OfferVersionModel(
    val id: Long = -1,
    val offerId: Long = -1,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: MoneyModel = MoneyModel(),
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
}
