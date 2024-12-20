package com.wutsi.koki.sdk

import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.dto.CreateServiceResponse
import com.wutsi.koki.service.dto.GetServiceResponse
import com.wutsi.koki.service.dto.SearchServiceResponse
import com.wutsi.koki.service.dto.ServiceSortBy
import com.wutsi.koki.service.dto.UpdateServiceRequest
import org.springframework.web.client.RestTemplate

class KokiServices(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/services"
    }

    fun service(id: String): GetServiceResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetServiceResponse::class.java).body
    }

    fun services(
        ids: List<String>,
        names: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
        sortBy: ServiceSortBy?,
        ascending: Boolean,
    ): SearchServiceResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "name" to names,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
                "sort-by" to sortBy,
                "asc" to ascending
            )
        )
        return rest.getForEntity(url, SearchServiceResponse::class.java).body
    }

    fun delete(id: String) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun create(request: CreateServiceRequest): CreateServiceResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateServiceResponse::class.java).body!!
    }

    fun update(id: String, request: UpdateServiceRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }
}
