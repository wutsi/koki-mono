package com.wutsi.koki.webscraping.server.mapper

import com.wutsi.koki.webscraping.dto.Website
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import org.springframework.stereotype.Service

@Service
class WebsiteMapper {
    fun toWebsite(entity: WebsiteEntity): Website {
        return Website(
            id = entity.id ?: -1,
            userId = entity.userId,
            baseUrl = entity.baseUrl,
            baseUrlHash = entity.baseUrlHash,
            listingUrlPrefix = entity.listingUrlPrefix,
            homeUrls = entity.homeUrls,
            contentSelector = entity.contentSelector,
            imageSelector = entity.imageSelector,
            active = entity.active,
            createdAt = entity.createdAt,
        )
    }
}
