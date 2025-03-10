package com.wutsi.koki.sdk

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import org.springframework.web.client.RestTemplate

class KokiFiles(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val accessTokenProvider: AccessTokenProvider,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/files"
    }

    fun file(id: Long): GetFileResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFileResponse::class.java).body
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun uploadUrl(
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        workflowInstanceId: String? = null,
        formId: String? = null,
    ): String {
        return urlBuilder.build(
            "$PATH_PREFIX/upload",
            mapOf(
                "workflow-instance-id" to workflowInstanceId,
                "form-id" to formId,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "tenant-id" to tenantProvider.id(),
                "access-token" to accessTokenProvider.accessToken()
            )
        )
    }

    fun files(
        ids: List<Long>,
        ownerId: Long?,
        ownerType: ObjectType?,
        limit: Int,
        offset: Int,
    ): SearchFileResponse {
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
        return rest.getForEntity(url, SearchFileResponse::class.java).body
    }
}
