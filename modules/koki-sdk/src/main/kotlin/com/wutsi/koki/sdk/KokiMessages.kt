package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import org.springframework.web.client.RestTemplate

class KokiMessages(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/messages"
    }

    fun send(request: SendMessageRequest): SendMessageResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, SendMessageResponse::class.java).body!!
    }

    fun message(id: Long): GetMessageResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetMessageResponse::class.java).body!!
    }

    fun messages(
        ids: List<Long>,
        ownerId: Long?,
        ownerType: ObjectType?,
        statuses: List<MessageStatus>,
        limit: Int,
        offset: Int,
    ): SearchMessageResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "status" to statuses,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchMessageResponse::class.java).body!!
    }

    fun status(id: Long, request: UpdateMessageStatusRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/status")
        rest.postForEntity(url, request, Any::class.java)
    }
}
