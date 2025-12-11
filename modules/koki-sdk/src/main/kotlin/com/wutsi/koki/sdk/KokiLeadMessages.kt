package com.wutsi.koki.sdk

import com.wutsi.koki.lead.dto.GetLeadMessageResponse
import com.wutsi.koki.lead.dto.SearchLeadMessageResponse
import org.springframework.web.client.RestTemplate
import java.util.Collections.emptyList

class KokiLeadMessages(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/lead-messages"
    }

    fun get(id: Long): GetLeadMessageResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetLeadMessageResponse::class.java).body!!
    }

    fun search(
        ids: List<Long> = emptyList(),
        leadIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): SearchLeadMessageResponse {
        val url = urlBuilder.build(
            path = PATH_PREFIX,
            parameters = mapOf(
                "id" to ids,
                "lead-id" to leadIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchLeadMessageResponse::class.java).body!!
    }
}
