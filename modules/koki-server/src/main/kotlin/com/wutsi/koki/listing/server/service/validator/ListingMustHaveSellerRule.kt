package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveSellerAgentCommissionRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (listing.sellerAgentCommission == null || listing.sellerAgentCommission == 0.0) {
            throw ValidationException(ErrorCode.LISTING_MISSING_SELLER_COMMISSION)
        }
    }
}
