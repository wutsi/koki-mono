package com.wutsi.koki.portal.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.portal.mapper.FormMapper
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.page.settings.form.FormForm
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.stereotype.Service

@Service
class FormService(
    private val koki: KokiForms,
    private val mapper: FormMapper,
    private val objectMapper: ObjectMapper,
) {
    fun form(
        id: String,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
    ): FormModel {
        return mapper.toFormModel(koki.form(id).form, workflowInstanceId, activityInstanceId)
    }

    fun delete(id: String) {
        koki.delete(id)
    }

    fun create(form: FormForm): String {
        return koki.create(
            SaveFormRequest(
                active = form.active,
                content = FormContent(
                    title = form.title,
                    name = form.name,
                    elements = objectMapper.readValue(form.elements, Array<FormElement>::class.java).toList()
                )
            )
        ).formId
    }

    fun update(id: String, form: FormForm) {
        koki.update(
            id,
            SaveFormRequest(
                active = form.active,
                content = FormContent(
                    title = form.title,
                    name = form.name,
                    elements = objectMapper.readValue(form.elements, Array<FormElement>::class.java).toList()
                )
            )
        )
    }

    fun forms(
        ids: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: FormSortBy? = null,
        ascending: Boolean = true,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
    ): List<FormModel> {
        return koki.forms(
            ids = ids,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        ).forms.map { form -> mapper.toFormModel(form, workflowInstanceId, activityInstanceId) }
    }

    fun html(
        formId: String,
        formDataId: String? = null,
        roleName: String? = null,
        workflowInstanceId: String? = null,
        activityInstanceId: String? = null,
        readOnly: Boolean = false
    ): String {
        return koki.html(
            formId = formId,
            formDataId = formDataId,
            roleName = roleName,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            readOnly = readOnly,
        )
    }

    fun submit(
        formId: String,
        workflowInstanceId: String?,
        activityInstanceId: String?,
        data: Map<String, Any>
    ): String {
        return koki.submit(
            SubmitFormDataRequest(
                formId = formId,
                data = data,
                workflowInstanceId = workflowInstanceId,
                activityInstanceId = activityInstanceId,
            )
        ).formDataId
    }

    fun submit(
        formDataId: String,
        activityInstanceId: String?,
        data: Map<String, Any>
    ) {
        koki.submit(
            formDataId,
            UpdateFormDataRequest(
                data = data,
                activityInstanceId = activityInstanceId
            )
        )
    }
}
