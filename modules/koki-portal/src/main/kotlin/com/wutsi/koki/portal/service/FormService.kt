package com.wutsi.koki.portal.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.SaveFormRequest
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.portal.mapper.FormMapper
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.FormSubmissionModel
import com.wutsi.koki.portal.page.settings.form.FormForm
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.workflow.dto.FormSortBy
import org.springframework.stereotype.Service

@Service
class FormService(
    private val koki: KokiForms,
    private val mapper: FormMapper,
    private val userService: UserService,
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
                content = objectMapper.readValue(form.json, FormContent::class.java)
            )
        ).formId
    }

    fun update(id: String, form: FormForm) {
        koki.update(
            id,
            SaveFormRequest(
                active = form.active,
                content = objectMapper.readValue(form.json, FormContent::class.java),
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

    fun submission(id: String): FormSubmissionModel {
        val entity = koki.submission(id).formSubmission
        val user = entity.submittedById?.let { id -> userService.user(id) }
        val form = forms(
            limit = 1,
            ids = listOf(entity.formId)
        ).firstOrNull() ?: FormModel(id = entity.formId)

        return mapper.toFormSubmissionModel(entity, form, user)
    }

    fun submissions(
        formId: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<FormSubmissionModel> {
        val submissions = koki.submissions(
            formId = formId,
            limit = limit,
            offset = offset
        ).formSubmissions

        val userIds = submissions.mapNotNull { submission -> submission.submittedById }.toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return submissions.map { submission ->
            mapper.toFormSubmissionModel(
                entity = submission,
                submittedBy = submission.submittedById?.let { id -> userMap[id] }
            )
        }
    }
}
