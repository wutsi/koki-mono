package com.wutsi.koki.sdk

import com.wutsi.koki.webscraping.dto.GetWebpageResponse
import com.wutsi.koki.webscraping.dto.SearchWebpagesResponse
import org.springframework.web.client.RestTemplate

class KokiWebpages(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/webpages"
    }

    fun get(id: Long): GetWebpageResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetWebpageResponse::class.java).body!!
    }

    fun search(
        websiteId: Long? = null,
        listingId: Long? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchWebpagesResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "website-id" to websiteId,
                "listing-id" to listingId,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            ),
        )
        return rest.getForEntity(url, SearchWebpagesResponse::class.java).body!!
    }
}
