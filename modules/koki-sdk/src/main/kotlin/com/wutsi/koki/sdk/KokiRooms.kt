package com.wutsi.koki.sdk

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.CreateInvitationRequest
import com.wutsi.koki.account.dto.CreateInvitationResponse
import com.wutsi.koki.account.dto.CreateUserRequest
import com.wutsi.koki.account.dto.CreateUserResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.account.dto.GetInvitationResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.SearchAttributeResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.common.dto.ImportResponse
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

class KokiAccounts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val ACCOUNT_PATH_PREFIX = "/v1/accounts"
        private const val ATTRIBUTE_PATH_PREFIX = "/v1/attributes"
        private const val INVITATION_PATH_PREFIX = "/v1/invitations"
    }

    fun create(request: CreateAccountRequest): CreateAccountResponse {
        val url = urlBuilder.build(ACCOUNT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateAccountResponse::class.java).body
    }

    fun update(id: Long, request: UpdateAccountRequest) {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun account(id: Long): GetAccountResponse {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAccountResponse::class.java).body
    }

    fun accounts(
        keyword: String?,
        ids: List<Long>,
        accountTypeIds: List<Long>,
        managedByIds: List<Long>,
        createdByIds: List<Long>,
        userIds: List<Long>,
        limit: Int,
        offset: Int,
    ): SearchAccountResponse {
        val url = urlBuilder.build(
            ACCOUNT_PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "account-type-id" to accountTypeIds,
                "managed-by-id" to managedByIds,
                "created-by-id" to createdByIds,
                "user-id" to userIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAccountResponse::class.java).body
    }

    fun attribute(id: Long): GetAttributeResponse {
        val url = urlBuilder.build("$ATTRIBUTE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAttributeResponse::class.java).body
    }

    fun attributes(
        ids: List<Long>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchAttributeResponse {
        val url = urlBuilder.build(
            ATTRIBUTE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAttributeResponse::class.java).body
    }

    fun uploadAttributes(file: MultipartFile): ImportResponse {
        val url = urlBuilder.build("$ATTRIBUTE_PATH_PREFIX/csv")
        return upload(url, file, ImportResponse::class.java)
    }

    fun createUser(accountId: Long, request: CreateUserRequest): CreateUserResponse {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$accountId/user")
        return rest.postForEntity(url, request, CreateUserResponse::class.java).body
    }

    fun createInvitation(request: CreateInvitationRequest): CreateInvitationResponse {
        val url = urlBuilder.build(INVITATION_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateInvitationResponse::class.java).body
    }

    fun invitation(id: String): GetInvitationResponse {
        val url = urlBuilder.build("$INVITATION_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetInvitationResponse::class.java).body
    }
}
