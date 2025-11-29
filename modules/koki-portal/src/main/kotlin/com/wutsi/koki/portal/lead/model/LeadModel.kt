package com.wutsi.koki.portal.lead.model

import com.wutsi.koki.lead.dto.LeadSource
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
    val phoneNumberFormatted: String? = null,
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
    val source: LeadSource = LeadSource.UNKNOWN,
) {
    val statusNew: Boolean
        get() = status == LeadStatus.NEW

    val phoneNumberUrl: String?
        get() = phoneNumber.ifEmpty { null }?.let { number -> "tel:$number" }

    val emailUrl: String?
        get() = email?.ifEmpty { null }?.let { email -> "mailto:$email" }

    val whatsappUrl: String?
        get() = phoneNumber.ifEmpty { null }?.let { number -> "https://wa.me/" + number.substring(1) }

    val messageHtml: String?
        get() = message?.let { msg -> HtmlUtils.toHtml(msg) }
}
