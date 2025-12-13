package com.wutsi.koki.portal.lead.model

import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class LeadModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val listing: ListingModel? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val lastMessage: LeadMessageModel = LeadMessageModel(),
    val nextContactAt: Date? = null,
    val nextContactAtText: String? = null,
    val nextVisitAt: Date? = null,
    val nextVisitAtText: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val source: LeadSource = LeadSource.UNKNOWN,
    val address: AddressModel? = null,
    val totalMessages: Int? = null,
) {
    val statusNew: Boolean
        get() = status == LeadStatus.NEW

    val statusContactLater: Boolean
        get() = status == LeadStatus.CONTACT_LATER

    val statusVisitSet: Boolean
        get() = status == LeadStatus.VISIT_SET

    val readOnly: Boolean
        get() = false
}
