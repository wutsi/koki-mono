package com.wutsi.koki.portal.listing.form

import com.wutsi.koki.listing.dto.ListingStatus
import java.time.LocalDate

data class ListingStatusForm(
    val id: Long = -1,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val buyerPhone: String? = null,
    val buyerAgentUserId: Long? = null,
    val comment: String? = null,
    val transactionDate: LocalDate? = null,
)
