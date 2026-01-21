package com.wutsi.koki.portal.webscaping.service

import com.wutsi.koki.portal.webscaping.mapper.WebsiteMapper
import com.wutsi.koki.portal.webscaping.model.WebsiteModel
import com.wutsi.koki.sdk.KokiWebsites
import org.springframework.stereotype.Service

@Service
class WebsiteService(
    private val koki: KokiWebsites,
    private val mapper: WebsiteMapper,
) {
    fun get(webpageId: Long): WebsiteModel {
        val webpage = koki.get(webpageId).website
        return mapper.toWebsiteModel(webpage)
    }
}
