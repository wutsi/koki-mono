package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
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

    fun roles(
        ids: List<Long> = emptyList()
    ): SearchRoleResponse {
        val url = urlBuilder.build(
            ROLE_PATH_PREFIX,
            mapOf(
                "id" to ids,
            )
        )
        return rest.getForEntity(url, SearchRoleResponse::class.java).body!!
    }
}
