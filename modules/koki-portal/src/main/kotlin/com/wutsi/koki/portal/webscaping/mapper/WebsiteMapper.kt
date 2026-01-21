package com.wutsi.koki.portal.webscaping.mapper

import com.wutsi.koki.portal.webscaping.model.WebsiteModel
import com.wutsi.koki.webscraping.dto.Website
import org.springframework.stereotype.Service

@Service
class WebsiteMapper {
    fun toWebsiteModel(entity: Website): WebsiteModel {
        return WebsiteModel(
            id = entity.id,
            userId = entity.userId,
            baseUrl = entity.baseUrl,
            listingUrlPrefix = entity.listingUrlPrefix,
            homeUrls = entity.homeUrls,
            contentSelector = entity.contentSelector,
            imageSelector = entity.imageSelector,
            active = entity.active,
            createdAt = entity.createdAt,
        )
    }
}
