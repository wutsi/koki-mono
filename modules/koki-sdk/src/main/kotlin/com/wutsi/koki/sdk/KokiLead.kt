package com.wutsi.koki.sdk

import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.GetLeadResponse
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.SearchLeadResponse
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import org.springframework.web.client.RestTemplate
import java.util.Collections.emptyList

class KokiLead(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/leads"
    }

    fun get(id: Long): GetLeadResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetLeadResponse::class.java).body!!
    }

    fun create(request: CreateLeadRequest): CreateLeadResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateLeadResponse::class.java).body!!
    }

    fun updateStatus(id: Long, request: UpdateLeadStatusRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun search(
        ids: List<Long> = emptyList(),
        userId: Long? = null,
        listingIds: List<Long> = emptyList(),
        agentUserIds: List<Long> = emptyList(),
        statuses: List<LeadStatus> = emptyList(),
        keywords: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchLeadResponse {
        val url = urlBuilder.build(
            path = PATH_PREFIX,
            parameters = mapOf(
                "q" to keywords,
                "id" to ids,
                "user-id" to userId,
                "listing-id" to listingIds,
                "agent-user-id" to agentUserIds,
                "status" to statuses,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchLeadResponse::class.java).body
    }
}
