package com.wutsi.koki.webscraping.server.mapper

import com.wutsi.koki.webscraping.dto.Webpage
import com.wutsi.koki.webscraping.dto.WebpageSummary
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import org.springframework.stereotype.Service

@Service
class WebpageMapper {
    fun toWebpageSummary(entity: WebpageEntity): WebpageSummary {
        return WebpageSummary(
            id = entity.id ?: -1,
            websiteId = entity.website.id ?: -1,
            listingId = entity.listingId,
            url = entity.url,
            imageUrl = entity.imageUrls.firstOrNull(),
            active = entity.active,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun toWebpage(entity: WebpageEntity): Webpage {
        return Webpage(
            id = entity.id ?: -1,
            websiteId = entity.website.id ?: -1,
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
