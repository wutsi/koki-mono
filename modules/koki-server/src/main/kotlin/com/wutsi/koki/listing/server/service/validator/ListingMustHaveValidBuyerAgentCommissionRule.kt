package com.wutsi.koki.listing.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublishRule
import jakarta.validation.ValidationException

class ListingMustHaveValidBuyerAgentCommissionRule : ListingPublishRule {
    override fun validate(listing: ListingEntity) {
        if (listing.sellerAgentCommission == null || listing.sellerAgentCommission == 0.0) {
            return // Will be handled by ListingMustHaveSellerAgentCommissionRule
        } else if (
            listing.buyerAgentCommission != null &&
            listing.buyerAgentCommission!! >= listing.sellerAgentCommission!!
        ) {
            throw ValidationException(ErrorCode.LISTING_INVALID_BUYER_COMMISSION)
        }
    }
}
