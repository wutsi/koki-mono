package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import org.springframework.web.client.RestTemplate

class KokiUsers(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val USER_PATH_PREFIX = "/v1/users"
        private const val ROLE_PATH_PREFIX = "/v1/roles"
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
        permissions: List<String> = emptyList(),
        status: UserStatus? = null,
        limit: Int = 20,
        offset: Int = 0
    ): SearchUserResponse {
        val url = urlBuilder.build(
            USER_PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "role-id" to roleIds,
                "permission" to permissions,
                "status" to status,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchUserResponse::class.java).body!!
    }

    fun createUser(request: CreateUserRequest): CreateUserResponse {
        val url = urlBuilder.build(USER_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateUserResponse::class.java).body
    }

    fun updateUser(id: Long, request: UpdateUserRequest) {
        val url = urlBuilder.build("$USER_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun roles(
        ids: List<Long> = emptyList(),
        active: Boolean?,
        limit: Int = 20,
        offset: Int = 0
    ): SearchRoleResponse {
        val url = urlBuilder.build(
            ROLE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "active" to active,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchRoleResponse::class.java).body!!
    }

    fun createRole(request: CreateRoleRequest): CreateRoleResponse {
        val url = urlBuilder.build(ROLE_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateRoleResponse::class.java).body
    }

    fun updateRole(id: Long, request: UpdateRoleRequest) {
        val url = urlBuilder.build("$ROLE_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun deleteRole(id: Long) {
        val url = urlBuilder.build("$ROLE_PATH_PREFIX/$id")
        rest.delete(url)
    }
}
