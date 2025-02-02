package com.wutsi.koki.sdk

import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.GetFormSubmissionResponse
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SaveFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.form.dto.SearchFormSubmissionResponse
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.web.client.RestTemplate

class KokiForms(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
) {
    companion object {
        private const val FORM_PATH_PREFIX = "/v1/forms"
        private const val FORM_DATA_PATH_PREFIX = "/v1/form-data"
        private const val FORM_SUBMISSION_PATH_PREFIX = "/v1/form-submissions"
    }

    fun form(id: String): GetFormResponse {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormResponse::class.java).body
    }

    fun forms(
        ids: List<String>,
        active: Boolean?,
        limit: Int,
        offset: Int,
        sortBy: FormSortBy?,
        ascending: Boolean,
    ): SearchFormResponse {
        val url = urlBuilder.build(
            FORM_PATH_PREFIX,
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

    fun submission(id: String): GetFormSubmissionResponse {
        val url = urlBuilder.build("$FORM_SUBMISSION_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormSubmissionResponse::class.java).body
    }

    fun submissions(
        formId: String,
        limit: Int,
        offset: Int,
    ): SearchFormSubmissionResponse {
        val url = urlBuilder.build(
            FORM_SUBMISSION_PATH_PREFIX,
            mapOf(
                "form-id" to formId,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchFormSubmissionResponse::class.java).body
    }

    fun html(
        formId: String,
        formDataId: String?,
        roleName: String?,
        workflowInstanceId: String?,
        activityInstanceId: String?,
        readOnly: Boolean,
        preview: Boolean
    ): String {
        val tenantId = tenantProvider.id()
        val path = if (formDataId == null) {
            "$FORM_PATH_PREFIX/html/$tenantId/$formId.html"
        } else {
            "$FORM_PATH_PREFIX/html/$tenantId/$formId/$formDataId.html"
        }

        val url = urlBuilder.build(
            path,
            mapOf(
                "role-name" to roleName,
                "workflow-instance-id" to workflowInstanceId,
                "activity-instance-id" to activityInstanceId,
                "read-only" to readOnly,
                "preview" to preview,
            )
        )
        return rest.getForEntity(url, String::class.java).body
    }

    fun delete(formId: String) {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$formId")
        rest.delete(url)
    }

    fun create(request: SaveFormRequest): SaveFormResponse {
        val url = urlBuilder.build(FORM_PATH_PREFIX)
        return rest.postForEntity(url, request, SaveFormResponse::class.java).body!!
    }

    fun update(formId: String, request: SaveFormRequest) {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$formId")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun submit(request: SubmitFormDataRequest): SubmitFormDataResponse {
        val path = FORM_DATA_PATH_PREFIX
        val url = urlBuilder.build(path)
        return rest.postForEntity(url, request, SubmitFormDataResponse::class.java).body
    }

    fun submit(formDataId: String, request: UpdateFormDataRequest) {
        val url = urlBuilder.build("$FORM_DATA_PATH_PREFIX/$formDataId")
        rest.postForEntity(url, request, Any::class.java)
    }
}
