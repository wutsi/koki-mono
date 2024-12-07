package com.wutsi.koki.sdk

import com.wutsi.koki.workflow.dto.GetLogEntryResponse
import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import org.springframework.web.client.RestTemplate

class KokiLogs(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/logs"
    }

    fun log(id: String): GetLogEntryResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetLogEntryResponse::class.java).body
    }

    fun logs(
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
        limit: Int,
        offset: Int,
    ): SearchLogEntryResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "workflow-instance-id" to workflowInstanceId,
                "activity-instance-id" to activityInstanceId,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchLogEntryResponse::class.java).body
    }
}
