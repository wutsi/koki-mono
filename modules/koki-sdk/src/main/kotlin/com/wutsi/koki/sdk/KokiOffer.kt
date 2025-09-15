package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.SearchOfferResponse
import org.springframework.web.client.RestTemplate
import java.util.Collections.emptyList

class KokiOffer(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/offers"
    }

    fun create(request: CreateOfferRequest): CreateOfferResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateOfferResponse::class.java).body
    }

    fun get(id: Long): GetOfferResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetOfferResponse::class.java).body
    }

    fun search(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        agentUserId: Long? = null,
        statuses: List<OfferStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): SearchOfferResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "agent-user-id" to agentUserId,
                "status" to statuses,
                "limit" to limit,
                "offset" to offset,
            ),
        )
        return rest.getForEntity(url, SearchOfferResponse::class.java).body
    }
}
