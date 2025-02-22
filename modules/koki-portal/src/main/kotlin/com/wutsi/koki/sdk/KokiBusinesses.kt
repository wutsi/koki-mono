package com.wutsi.koki.sdk

import com.wutsi.koki.tenant.dto.GetBusinessResponse
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import org.springframework.web.client.RestTemplate

class KokiBusinesses(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/businesses"
    }

    fun business(): GetBusinessResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.getForEntity(url, GetBusinessResponse::class.java).body
    }

    fun save(request: SaveBusinessRequest) {
        val url = urlBuilder.build(PATH_PREFIX)
        rest.postForEntity(url, request, Any::class.java)
    }
}
