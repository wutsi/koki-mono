package com.wutsi.koki.sdk

import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import com.wutsi.koki.account.dto.UpdateAccountRequest
import org.springframework.web.client.RestTemplate

class KokiAccounts(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/accounts"
    }

    fun create(request: CreateAccountRequest): CreateAccountResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateAccountResponse::class.java).body
    }

    fun update(id: Long, request: UpdateAccountRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, CreateAccountResponse::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun account(id: Long): GetAccountResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAccountResponse::class.java).body
    }

    fun accounts(
        keyword: String?,
        ids: List<Long>,
        managedByIds: List<Long>,
        createdByIds: List<Long>,
        limit: Int = 20,
        offset: Int = 0
    ): SearchAccountResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
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
}
