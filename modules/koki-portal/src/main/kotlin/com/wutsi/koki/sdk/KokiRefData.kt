package com.wutsi.koki.sdk

import com.wutsi.koki.refdata.dto.SearchUnitResponse
import org.springframework.web.client.RestTemplate

class KokiRefData(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val UNIT_PATH_PREFIX = "/v1/units"
    }

    fun units(): SearchUnitResponse {
        val url = urlBuilder.build(UNIT_PATH_PREFIX)
        return rest.getForEntity(url, SearchUnitResponse::class.java).body
    }
}
