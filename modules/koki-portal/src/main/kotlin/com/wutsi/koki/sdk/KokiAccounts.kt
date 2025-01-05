package com.wutsi.koki.sdk

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.GetAccountTypeResponse
import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.SearchAccountTypeResponse
import com.wutsi.koki.account.dto.SearchAttributeResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import org.springframework.web.client.RestTemplate

class KokiAccounts(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val ACCOUNT_PATH_PREFIX = "/v1/accounts"
        private const val ACCOUNT_TYPE_PATH_PREFIX = "/v1/account-types"
        private const val ATTRIBUTE_PATH_PREFIX = "/v1/attributes"
    }

    fun create(request: CreateAccountRequest): CreateAccountResponse {
        val url = urlBuilder.build(ACCOUNT_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateAccountResponse::class.java).body
    }

    fun update(id: Long, request: UpdateAccountRequest) {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$id")
        rest.postForEntity(url, request, CreateAccountResponse::class.java)
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
        managedByIds: List<Long>,
        createdByIds: List<Long>,
        limit: Int,
        offset: Int,
    ): SearchAccountResponse {
        val url = urlBuilder.build(
            ACCOUNT_PATH_PREFIX,
            mapOf(
                "q" to keyword,
                "id" to ids,
                "managed-by-id" to managedByIds,
                "created-by-id" to createdByIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAccountResponse::class.java).body
    }

    fun type(id: Long): GetAccountTypeResponse {
        val url = urlBuilder.build("$ACCOUNT_TYPE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAccountTypeResponse::class.java).body
    }

    fun types(
        ids: List<Long>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
    ): SearchAccountTypeResponse {
        val url = urlBuilder.build(
            ACCOUNT_TYPE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAccountTypeResponse::class.java).body
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
}
