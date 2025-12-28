package com.wutsi.koki.sdk

import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.agent.dto.SearchAgentResponse
import org.springframework.web.client.RestTemplate

class KokiAgents(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/agents"
        private const val USER_PATH_PREFIX = "/v1/users"
    }

    fun get(id: Long): GetAgentResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAgentResponse::class.java).body!!
    }

    fun getByUser(userId: Long): GetAgentResponse {
        val url = urlBuilder.build("$USER_PATH_PREFIX/$userId/agent")
        return rest.getForEntity(url, GetAgentResponse::class.java).body!!
    }

    fun search(
        ids: List<Long> = emptyList(),
        userIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): SearchAgentResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "user-id" to userIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchAgentResponse::class.java).body!!
    }
}
