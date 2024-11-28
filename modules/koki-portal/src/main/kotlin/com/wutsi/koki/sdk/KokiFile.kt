package com.wutsi.koki.sdk

import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.CreateFileResponse
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import org.springframework.web.client.RestTemplate

class KokiFile(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/files"
    }

    fun create(request: CreateFileRequest): CreateFileResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateFileResponse::class.java).body
    }

    fun get(id: String): GetFileResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFileResponse::class.java).body
    }

    fun search(
        ids: List<String>,
        workflowInstanceIds: List<String>,
        formIds: List<String>,
        limit: Int,
        offset: Int,
    ): SearchFileResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "workflow-instance-id" to workflowInstanceIds,
                "form-id" to formIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchFileResponse::class.java).body
    }
}
