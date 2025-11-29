package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.CreateInvitationRequest
import com.wutsi.koki.tenant.dto.CreateInvitationResponse
import com.wutsi.koki.tenant.dto.GetInvitationResponse
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.SearchInvitationResponse
import org.springframework.web.client.RestTemplate
import java.util.Collections.emptyList

class KokiInvitations(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/invitations"
    }

    fun search(
        ids: List<String> = emptyList(),
        statuses: List<InvitationStatus> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): SearchInvitationResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "status" to statuses,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchInvitationResponse::class.java).body!!
    }

    fun create(request: CreateInvitationRequest): CreateInvitationResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateInvitationResponse::class.java).body!!
    }

    fun get(id: String): GetInvitationResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetInvitationResponse::class.java).body!!
    }

    fun delete(id: String) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }
}
