package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveSellerRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (
            listing.sellerName.isNullOrEmpty() ||
            listing.sellerEmail.isNullOrEmpty() ||
            listing.sellerPhone.isNullOrEmpty()
        ) {
            throw ValidationException(ErrorCode.LISTING_MISSING_SELLER)
        }
    }
}
