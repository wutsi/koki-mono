package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.FormMapper
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.stereotype.Service

@Service
class FormService(
    private val mapper: FormMapper,
    private val kokiForm: KokiForms
) {
    fun form(
        id: String,
        workflowInstanceId: String?,
        activityInstanceId: String?,
    ): FormModel {
        return mapper.toFormModel(kokiForm.getForm(id).form, workflowInstanceId, activityInstanceId)
    }

    fun forms(
        ids: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: FormSortBy? = null,
        ascending: Boolean = true,
        workflowInstanceId: String?,
        activityInstanceId: String?,
    ): List<FormModel> {
        return kokiForm.searchForms(
            ids = ids,
            active = active,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        ).forms.map { form -> mapper.toFormModel(form, workflowInstanceId, activityInstanceId) }
    }
}
