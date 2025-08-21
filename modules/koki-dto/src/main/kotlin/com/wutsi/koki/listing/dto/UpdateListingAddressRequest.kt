package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.Address
import jakarta.validation.Valid

data class UpdateListingAddressRequest(
    @get:Valid val address: Address? = null
)
