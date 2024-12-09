package com.wutsi.koki.sdk

import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.CreateMessageResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.MessageSortBy
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.dto.UpdateMessageRequest
import org.springframework.web.client.RestTemplate

class KokiMessages(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/messages"
    }

    fun message(id: String): GetMessageResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetMessageResponse::class.java).body
    }

    fun messages(
        ids: List<String>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
        sortBy: MessageSortBy?,
        ascending: Boolean
    ): SearchMessageResponse {
        val url = urlBuilder.build(
            PATH_PREFIX, mapOf(
                "id" to ids,
                "names" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
                "sort-by" to sortBy,
                "asc" to ascending
            )
        )
        return rest.getForEntity(url, SearchMessageResponse::class.java).body
    }

    fun create(request: CreateMessageRequest): CreateMessageResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateMessageResponse::class.java).body
    }

    fun update(id: String, request: UpdateMessageRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: String) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.delete(url)
    }
}
