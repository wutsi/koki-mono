package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveAddressRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (
            listing.country.isNullOrEmpty() ||
            listing.cityId == null ||
            listing.neighbourhoodId == null
        ) {
            throw ValidationException(ErrorCode.LISTING_MISSING_ADDRESS)
        }
    }
}
