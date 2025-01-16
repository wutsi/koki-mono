package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import org.springframework.web.client.RestTemplate

class KokiTenants(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val TENANT_PATH_PREFIX = "/v1/tenants"
        private const val CONFIG_PATH_PREFIX = "/v1/configurations"
    }

    fun tenants(): SearchTenantResponse {
        val url = urlBuilder.build(TENANT_PATH_PREFIX)
        return rest.getForEntity(url, SearchTenantResponse::class.java).body
    }

    fun save(request: SaveConfigurationRequest) {
        val url = urlBuilder.build(CONFIG_PATH_PREFIX)
        rest.postForEntity(url, request, Any::class.java)
    }

    fun configurations(
        names: List<String>,
        keyword: String?,
    ): SearchConfigurationResponse {
        val url = urlBuilder.build(
            CONFIG_PATH_PREFIX,
            mapOf(
                "name" to names,
                "q" to keyword,
            )
        )
        return rest.getForEntity(url, SearchConfigurationResponse::class.java).body!!
    }
}
