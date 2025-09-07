package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.IDType
import jakarta.validation.constraints.Size

data class UpdateListingSellerRequest(
    val sellerContactId: Long? = -1,

    @Deprecated("") @get:Size(max = 50) var sellerName: String? = null,
    @Deprecated("") @get:Size(max = 30) var sellerPhone: String? = null,
    @Deprecated("") @get:Size(max = 255) var sellerEmail: String? = null,
    @Deprecated("") @get:Size(max = 30) var sellerIdNumber: String? = null,
    @Deprecated("") var sellerIdType: IDType? = null,
    @Deprecated("") @get:Size(max = 2) var sellerIdCountry: String? = null,
)
