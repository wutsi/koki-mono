package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

class KokiTenant(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val CONFIG_PATH_PREFIX = "/v1/configurations"
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
