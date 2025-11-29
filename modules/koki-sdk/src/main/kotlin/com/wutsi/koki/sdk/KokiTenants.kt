package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import org.springframework.web.client.RestTemplate

class KokiTenants(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/tenants"
    }

    fun tenant(id: Long): GetTenantResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetTenantResponse::class.java).body!!
    }

    fun tenants(): SearchTenantResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.getForEntity(url, SearchTenantResponse::class.java).body!!
    }
}
