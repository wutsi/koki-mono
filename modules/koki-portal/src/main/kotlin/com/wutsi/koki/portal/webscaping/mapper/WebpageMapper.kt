package com.wutsi.koki.portal.webscaping.mapper

import com.wutsi.koki.portal.webscaping.model.WebpageModel
import com.wutsi.koki.webscraping.dto.Webpage
import com.wutsi.koki.webscraping.dto.WebpageSummary
import org.springframework.stereotype.Service

@Service
class WebpageMapper {
    fun toWebpageModel(entity: WebpageSummary): WebpageModel {
        return WebpageModel(
            id = entity.id,
            websiteId = entity.websiteId,
            listingId = entity.listingId,
            url = entity.url,
            imageUrl = entity.imageUrl,
            active = entity.active,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun toWebpageModel(entity: Webpage): WebpageModel {
        return WebpageModel(
            id = entity.id,
            websiteId = entity.websiteId,
            listingId = entity.listingId,
            url = entity.url,
            imageUrls = entity.imageUrls,
            content = entity.content,
            active = entity.active,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}
