package com.wutsi.koki.sdk

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.ImportWorkflowResponse
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.WorkflowSortBy
import org.springframework.web.client.RestTemplate
import java.net.URL

class KokiWorkflow(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val ACTIVITY_PATH_PREFIX = "/v1/activities"
        private val WORKFLOW_PATH_PREFIX = "/v1/workflows"
    }

    fun import(request: ImportWorkflowRequest): ImportWorkflowResponse {
        val url = urlBuilder.build(WORKFLOW_PATH_PREFIX)
        return rest.postForEntity(url, request, ImportWorkflowResponse::class.java).body!!
    }

    fun import(id: Long, request: ImportWorkflowRequest): ImportWorkflowResponse {
        val url = urlBuilder.build("$WORKFLOW_PATH_PREFIX/$id")
        return rest.postForEntity(url, request, ImportWorkflowResponse::class.java).body!!
    }

    fun imageUrl(id: Long): String {
        val tenantId = tenantProvider.id()
        return urlBuilder.build(
            "$WORKFLOW_PATH_PREFIX/images/$tenantId.$id.png",
        )
    }

    fun json(id: Long): String {
        val tenantId = tenantProvider.id()
        val url = urlBuilder.build(
            "$WORKFLOW_PATH_PREFIX/json/$tenantId.$id.json",
        )
        val json = URL(url).readText()
        val data = objectMapper.readValue(json, Map::class.java)

        return ObjectMapper()
            .writer()
            .withDefaultPrettyPrinter()
            .writeValueAsString(data)
    }

    fun workflow(id: Long): GetWorkflowResponse {
        val url = urlBuilder.build(
            "$WORKFLOW_PATH_PREFIX/$id",
        )
        return rest.getForEntity(url, GetWorkflowResponse::class.java).body!!
    }

    fun workflows(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): SearchWorkflowResponse {
        val url = urlBuilder.build(
            WORKFLOW_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "limit" to limit,
                "offset" to offset,
                "sort-by" to WorkflowSortBy.TITLE,
            )
        )
        return rest.getForEntity(url, SearchWorkflowResponse::class.java).body!!
    }

    fun activities(
        ids: List<Long> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        type: ActivityType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchActivityResponse {
        val url = urlBuilder.build(
            ACTIVITY_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "workflow-id" to workflowIds,
                "type" to type?.name,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchActivityResponse::class.java).body!!
    }
}
