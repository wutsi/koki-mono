package com.wutsi.koki.listing.server.mapper

import com.wutsi.koki.listing.dto.AIListing
import com.wutsi.koki.listing.server.domain.AIListingEntity
import org.springframework.stereotype.Service

@Service
class AIListingMapper {
    fun toAIListing(entity: AIListingEntity): AIListing {
        return AIListing(
            id = entity.id ?: -1,
            listingId = entity.listing.id ?: -1,
            text = entity.text,
            result = entity.result,
        )
    }
}
