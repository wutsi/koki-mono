package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveGeolocationRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (listing.longitude == null || listing.latitude == null) {
            throw ValidationException(ErrorCode.LISTING_MISSING_GEOLOCATION)
        }
    }
}
