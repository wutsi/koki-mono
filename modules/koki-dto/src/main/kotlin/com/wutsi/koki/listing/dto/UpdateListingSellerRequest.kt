package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.IDType
import jakarta.validation.constraints.Size

data class UpdateListingSellerRequest(
    @get:Size(max = 50) var sellerName: String? = null,
    @get:Size(max = 30) var sellerPhone: String? = null,
    @get:Size(max = 255) var sellerEmail: String? = null,
    @get:Size(max = 30) var sellerIdNumber: String? = null,
    var sellerIdType: IDType? = null,
    @get:Size(max = 2) var sellerIdCountry: String? = null,
)
