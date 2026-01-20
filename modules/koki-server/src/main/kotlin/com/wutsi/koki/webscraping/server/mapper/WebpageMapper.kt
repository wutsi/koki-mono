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
            websiteId = entity.websiteId,
            url = entity.url,
            active = entity.active,
            createdAt = entity.createdAt,
        )
    }

    fun toWebpage(entity: WebpageEntity): Webpage {
        return Webpage(
            id = entity.id ?: -1,
            websiteId = entity.websiteId,
            url = entity.url,
            content = entity.content,
            imageUrls = entity.imageUrls,
            active = entity.active,
            createdAt = entity.createdAt,
        )
    }
}
