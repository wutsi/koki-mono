package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import com.wutsi.koki.tenant.dto.SetUserPhotoRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import org.springframework.web.client.RestTemplate

class KokiUsers(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/users"
    }

    fun user(id: Long): GetUserResponse {
        val url = urlBuilder.build(
            "$PATH_PREFIX/$id"
        )
        return rest.getForEntity(url, GetUserResponse::class.java).body!!
    }

    fun users(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        permissions: List<String> = emptyList(),
        status: UserStatus? = null,
        username: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): SearchUserResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "role-id" to roleIds,
                "permission" to permissions,
                "status" to status,
                "username" to username,
                "limit" to limit,
                "offset" to offset
            )
        )
        return rest.getForEntity(url, SearchUserResponse::class.java).body!!
    }

    fun create(request: CreateUserRequest): CreateUserResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateUserResponse::class.java).body
    }

    fun update(id: Long, request: UpdateUserRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun photo(id: Long, request: SetUserPhotoRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/photo")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun sendUsername(request: SendUsernameRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/username/send")
        rest.postForEntity(url, request, Any::class.java)
    }
}
