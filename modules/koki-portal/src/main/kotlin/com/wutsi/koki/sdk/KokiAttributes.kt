package com.wutsi.koki.sdk

import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.account.dto.SearchAttributeResponse
import org.springframework.web.client.RestTemplate

class KokiAttributes(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/attributes"
    }

    fun attribute(id: Long): GetAttributeResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAttributeResponse::class.java).body
    }

    fun attributes(
        ids: List<Long>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchAttributeResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAttributeResponse::class.java).body
    }
}
