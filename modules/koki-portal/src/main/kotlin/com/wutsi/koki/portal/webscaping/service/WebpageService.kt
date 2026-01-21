package com.wutsi.koki.portal.webscaping.service

import com.wutsi.koki.portal.webscaping.mapper.WebpageMapper
import com.wutsi.koki.portal.webscaping.model.WebpageModel
import com.wutsi.koki.sdk.KokiWebpages
import org.springframework.stereotype.Service

@Service
class WebpageService(
    private val koki: KokiWebpages,
    private val mapper: WebpageMapper,
) {
    fun get(webpageId: Long): WebpageModel {
        val webpage = koki.get(webpageId).webpage
        return mapper.toWebpageModel(webpage)
    }

    fun search(
        websiteId: Long? = null,
        listingId: Long? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<WebpageModel> {
        val webpages = koki.search(
            websiteId = websiteId,
            listingId = listingId,
            active = active,
            limit = limit,
            offset = offset
        ).webpages

        return webpages.map { webpage -> mapper.toWebpageModel(webpage) }
    }
}
