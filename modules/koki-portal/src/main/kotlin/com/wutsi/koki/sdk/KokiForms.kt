package com.wutsi.koki.sdk

import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SaveFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
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
    }

    fun getForm(id: String): GetFormResponse {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetFormResponse::class.java).body
    }

    fun getFormHtml(
        formId: String,
        formDataId: String?,
        roleName: String?,
        workflowInstanceId: String?,
        activityInstanceId: String?,
        readOnly: Boolean
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
            )
        )
        return rest.getForEntity(url, String::class.java).body
    }

    fun deleteForm(formId: String) {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$formId")
        rest.delete(url)
    }

    fun createForm(request: SaveFormRequest): SaveFormResponse {
        val url = urlBuilder.build(FORM_PATH_PREFIX)
        return rest.postForEntity(url, request, SaveFormResponse::class.java).body!!
    }

    fun updateForm(formId: String, request: SaveFormRequest) {
        val url = urlBuilder.build("$FORM_PATH_PREFIX/$formId")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun searchForms(
        ids: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: FormSortBy? = null,
        ascending: Boolean = true,
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

    fun submitData(request: SubmitFormDataRequest): SubmitFormDataResponse {
        val path = FORM_DATA_PATH_PREFIX
        val url = urlBuilder.build(path)
        return rest.postForEntity(url, request, SubmitFormDataResponse::class.java).body
    }

    fun updateData(formDataId: String, request: UpdateFormDataRequest) {
        val url = urlBuilder.build("$FORM_DATA_PATH_PREFIX/$formDataId")
        rest.postForEntity(url, request, Any::class.java)
    }
}
