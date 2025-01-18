package com.wutsi.koki.sdk

import com.wutsi.koki.module.dto.SearchModuleResponse
import com.wutsi.koki.module.dto.SearchPermissionResponse
import org.springframework.web.client.RestTemplate

class KokiModules(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val MODULE_PATH_PREFIX = "/v1/modules"
        private const val PERMISSION_PATH_PREFIX = "/v1/permission"
    }

    fun modules(): SearchModuleResponse {
        val url = urlBuilder.build(MODULE_PATH_PREFIX)
        return rest.getForEntity(url, SearchModuleResponse::class.java).body
    }

    fun permissions(
        ids: List<Long> = emptyList(),
        moduleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): SearchPermissionResponse {
        val url = urlBuilder.build(
            path = PERMISSION_PATH_PREFIX,
            parameters = mapOf(
                "id" to ids,
                "module-id" to moduleIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchPermissionResponse::class.java).body
    }
}
