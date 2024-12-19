package com.wutsi.koki.service.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.service.server.domain.ServiceEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class ServiceCaller(
    private val rest: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val authProvider: AuthorizationHeaderProvider,
) {
    @Suppress("UNCHECKED_CAST")
    fun call(
        service: ServiceEntity,
        method: HttpMethod,
        path: String? = null,
        input: Map<String, Any> = emptyMap(),
        workflowInstanceId: String? = null
    ): ServiceResponse {
        val url = path?.let { "${service.baseUrl}$path" } ?: service.baseUrl
        val payload = if (input.isNotEmpty()) objectMapper.writeValueAsString(input) else null
        val headers = createHeaders(service, workflowInstanceId)
        val entity = HttpEntity<String>(payload, headers)

        val response = rest.exchange(
            URI(url),
            method,
            entity,
            Map::class.java
        )
        return ServiceResponse(
            statusCode = response.statusCode,
            body = response.body as Map<String, Any>?
        )
    }

    private fun createHeaders(service: ServiceEntity, workflowInstanceId: String?): HttpHeaders {
        val headers = HttpHeaders()

        headers.contentType = MediaType.APPLICATION_JSON
        workflowInstanceId?.let { value -> headers.add("X-Workflow-Instance-ID", value) }
        authProvider.get(service.authorizationType)
            .value(service)
            ?.let { value -> headers.add("Authorization", value) }

        return headers
    }
}
