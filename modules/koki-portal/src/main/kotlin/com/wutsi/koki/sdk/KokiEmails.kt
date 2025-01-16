package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.GetEmailResponse
import com.wutsi.koki.email.dto.SearchEmailResponse
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import org.springframework.web.client.RestTemplate

class KokiEmails(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/emails"
    }

    fun send(request: SendEmailRequest): SendEmailResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, SendEmailResponse::class.java).body
    }

    fun email(id: String): GetEmailResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetEmailResponse::class.java).body
    }

    fun emails(
        ids: List<String>,
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int,
    ): SearchEmailResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchEmailResponse::class.java).body
    }
}
