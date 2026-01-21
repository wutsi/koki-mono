package com.wutsi.koki.sdk

import com.wutsi.koki.webscraping.dto.CreateWebsiteRequest
import com.wutsi.koki.webscraping.dto.CreateWebsiteResponse
import com.wutsi.koki.webscraping.dto.GetWebsiteResponse
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteResponse
import com.wutsi.koki.webscraping.dto.SearchWebsitesResponse
import com.wutsi.koki.webscraping.dto.UpdateWebsiteRequest
import org.springframework.web.client.RestTemplate

class KokiWebsites(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/websites"
    }

    fun create(request: CreateWebsiteRequest): CreateWebsiteResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateWebsiteResponse::class.java).body!!
    }

    fun update(id: Long, request: UpdateWebsiteRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun get(id: Long): GetWebsiteResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetWebsiteResponse::class.java).body!!
    }

    fun search(
        ids: List<Long> = emptyList(),
        userIds: List<Long> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchWebsitesResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "user-id" to userIds,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            ),
        )
        return rest.getForEntity(url, SearchWebsitesResponse::class.java).body!!
    }

    fun scrape(id: Long, request: ScrapeWebsiteRequest): ScrapeWebsiteResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id/scrape")
        return rest.postForEntity(url, request, ScrapeWebsiteResponse::class.java).body!!
    }
}
