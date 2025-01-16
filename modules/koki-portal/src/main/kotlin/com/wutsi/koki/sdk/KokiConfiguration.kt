package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.springframework.web.client.RestTemplate

class KokiConfiguration(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/configurations"
    }

    fun configurations(
        names: List<String>,
        keyword: String?,
    ): SearchConfigurationResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "name" to names,
                "q" to keyword,
            )
        )
        return rest.getForEntity(url, SearchConfigurationResponse::class.java).body!!
    }

    fun save(request: SaveConfigurationRequest) {
        val url = urlBuilder.build(PATH_PREFIX)
        rest.postForEntity(url, request, Any::class.java)
    }
}
