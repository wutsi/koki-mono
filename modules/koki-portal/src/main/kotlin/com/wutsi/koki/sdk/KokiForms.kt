package com.wutsi.koki.sdk

import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.web.client.RestTemplate

class KokiForms(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
) {
    companion object {
        private val PATH_PREFIX = "/v1/forms"
    }

    fun get(id: String): GetFormResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormResponse::class.java).body
    }

    fun html(
        formId: String,
        formDataId: String? = null,
        roleName: String? = null,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
    ): String {
        val tenantId = tenantProvider.id()
        val path = if (formDataId == null) {
            "$PATH_PREFIX/html/$tenantId/$formId.html"
        } else {
            "$PATH_PREFIX/html/$tenantId/$formId/$formDataId.html"
        }

        val url = urlBuilder.build(
            path,
            mapOf(
                "role-name" to roleName,
                "workflow-instance-id" to workflowInstanceId,
                "activity-instance-id" to activityInstanceId,
            )
        )
        return rest.getForEntity(url, String::class.java).body
    }

    fun search(
        ids: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: FormSortBy? = null,
        ascending: Boolean = true,
    ): SearchFormResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "active" to active,
                "limit" to limit,
                "offset" to offset,
                "sort-by" to sortBy,
                "asc" to ascending
            )
        )
        return rest.getForEntity(url, SearchFormResponse::class.java).body
    }
}
