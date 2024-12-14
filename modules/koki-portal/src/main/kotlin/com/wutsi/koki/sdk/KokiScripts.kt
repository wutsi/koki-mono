package com.wutsi.koki.sdk

import com.wutsi.koki.script.dto.CreateScriptRequest
import com.wutsi.koki.script.dto.CreateScriptResponse
import com.wutsi.koki.script.dto.ExecuteScriptRequest
import com.wutsi.koki.script.dto.ExecuteScriptResponse
import com.wutsi.koki.script.dto.GetScriptResponse
import com.wutsi.koki.script.dto.ScriptSortBy
import com.wutsi.koki.script.dto.SearchScriptResponse
import com.wutsi.koki.script.dto.UpdateScriptRequest
import org.springframework.web.client.RestTemplate

class KokiScripts(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/scripts"
    }

    fun script(id: String): GetScriptResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetScriptResponse::class.java).body
    }

    fun scripts(
        ids: List<String>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
        sortBy: ScriptSortBy?,
        ascending: Boolean,
    ): SearchScriptResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
                "sort-by" to sortBy,
                "asc" to ascending
            )
        )
        return rest.getForEntity(url, SearchScriptResponse::class.java).body
    }

    fun delete(id: String) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun create(request: CreateScriptRequest): CreateScriptResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateScriptResponse::class.java).body!!
    }

    fun update(id: String, request: UpdateScriptRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun execute(id: String, request: ExecuteScriptRequest): ExecuteScriptResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id/execute")
        return rest.postForEntity(url, request, ExecuteScriptResponse::class.java).body
    }
}