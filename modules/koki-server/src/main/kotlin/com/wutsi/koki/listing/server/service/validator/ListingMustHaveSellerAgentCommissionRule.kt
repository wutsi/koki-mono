package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHavePriceRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (listing.price == null || listing.price == 0L) {
            throw ValidationException(ErrorCode.LISTING_MISSING_PRICE)
        }
    }
}
