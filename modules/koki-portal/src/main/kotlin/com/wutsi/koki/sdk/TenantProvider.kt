package com.wutsi.koki.sdk

import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder

class KokiForms(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private val PATH_PREFIX = "/v1/forms"
    }

    fun get(id: String): GetFormResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormResponse::class.java).body
    }

    fun html(
        id: String,
        formId: String?,
        roleName: String? = null,
        tenantId: Long
    ): String {
        val url = urlBuilder.build(
            "$PATH_PREFIX/html/$tenantId/$id.html",
            mapOf(
                "aiid" to activityInstanceId,
                "submit-url" to URLEncoder.encode(submitUrl, "utf-8"),
                "role-name" to roleName
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
