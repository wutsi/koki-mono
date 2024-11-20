package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import org.springframework.web.client.RestTemplate

class KokiUser(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val USER_PATH_PREFIX = "/v1/users"
        private val ROLE_PATH_PREFIX = "/v1/roles"
    }

    fun user(id: Long): GetUserResponse {
        val url = urlBuilder.build(
            "$USER_PATH_PREFIX/$id"
        )
        return rest.getForEntity(url, GetUserResponse::class.java).body!!
    }

    fun users(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): SearchUserResponse {
        val url = urlBuilder.build(
            USER_PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "role-id" to roleIds,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchUserResponse::class.java).body!!
    }

    fun roles(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0
    ): SearchRoleResponse {
        val url = urlBuilder.build(
            ROLE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchRoleResponse::class.java).body!!
    }
}
