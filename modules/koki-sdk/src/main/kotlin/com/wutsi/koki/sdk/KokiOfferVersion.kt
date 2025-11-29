package com.wutsi.koki.sdk

import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionResponse
import com.wutsi.koki.offer.dto.GetOfferVersionResponse
import com.wutsi.koki.offer.dto.SearchOfferVersionResponse
import org.springframework.web.client.RestTemplate
import java.util.Collections.emptyList

class KokiOfferVersion(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/offer-versions"
    }

    fun create(request: CreateOfferVersionRequest): CreateOfferVersionResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateOfferVersionResponse::class.java).body!!
    }

    fun get(id: Long): GetOfferVersionResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetOfferVersionResponse::class.java).body!!
    }

    fun search(
        ids: List<Long> = emptyList(),
        offerId: Long? = null,
        agentUserId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchOfferVersionResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "offer-id" to offerId,
                "agent-user-id" to agentUserId,
                "limit" to limit,
                "offset" to offset,
            ),
        )
        return rest.getForEntity(url, SearchOfferVersionResponse::class.java).body!!
    }
}
