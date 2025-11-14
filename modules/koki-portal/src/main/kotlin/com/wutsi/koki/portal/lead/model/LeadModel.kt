package com.wutsi.koki.portal.lead.model

import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.listing.model.ListingModel
import java.util.Date

data class LeadModel(
    val id: Long = -1,
    val listing: ListingModel? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val email: String? = null,
    val phoneNumber: String = "",
    val phoneNumberFormatted: String = "",
    val message: String? = null,
    val visitRequestedAt: Date? = null,
    val nextContactAt: Date? = null,
    val nextVisitAt: Date? = null,
    val visitRequestedAtText: String? = null,
    val nextContactAtText: String? = null,
    val nextVisitAtText: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val whatsappUrl: String? = null
) {
    val statusNew: Boolean
        get() = status == LeadStatus.NEW

    val phoneNumberUrl: String?
        get() = phoneNumber.let { "tel:$phoneNumber" }

    val emailUrl: String?
        get() = email.let { "mailto:$email" }

    val messageHtml: String?
        get() = message?.let { msg -> HtmlUtils.toHtml(msg) }
}
