package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
import org.springframework.web.client.RestTemplate

class KokiRoles(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/roles"
    }

    fun roles(
        ids: List<Long> = emptyList(),
        active: Boolean?,
        limit: Int = 20,
        offset: Int = 0
    ): SearchRoleResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "active" to active,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchRoleResponse::class.java).body!!
    }

    fun create(request: CreateRoleRequest): CreateRoleResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateRoleResponse::class.java).body
    }

    fun update(id: Long, request: UpdateRoleRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }
}
