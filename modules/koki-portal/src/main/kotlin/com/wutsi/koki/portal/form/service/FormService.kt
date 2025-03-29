package com.wutsi.koki.portal.form.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.form.dto.CreateFormRequest
import com.wutsi.koki.form.dto.UpdateFormRequest
import com.wutsi.koki.portal.form.form.FormForm
import com.wutsi.koki.portal.form.mapper.FormMapper
import com.wutsi.koki.portal.form.model.FormModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiForms
import org.springframework.stereotype.Service

@Service
class FormService(
    private val koki: KokiForms,
    private val mapper: FormMapper,
    private val userService: UserService,
) {
    fun forms(
        ids: List<Long> = emptyList(),
        active: Boolean? = null,
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<FormModel> {
        val forms = koki.forms(
            ids = ids,
            active = active,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
        ).forms

        val userIds = forms.flatMap { form -> listOf(form.createdById, form.modifiedById) }
            .filterNotNull()
            .distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(ids = userIds, limit = userIds.size)
                .associateBy { user -> user.id }
        }

        return forms.map { form -> mapper.toFormModel(entity = form, users = users) }
    }

    fun form(id: Long, fullGraph: Boolean = true): FormModel {
        val form = koki.form(id).form

        val userIds = listOf(form.createdById, form.modifiedById)
            .filterNotNull()
            .distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(ids = userIds, limit = userIds.size)
                .associateBy { user -> user.id }
        }

        return mapper.toFormModel(entity = form, users = users)
    }

    fun create(form: FormForm): Long {
        return koki.create(
            request = CreateFormRequest(
                name = form.name,
                description = form.description,
                active = form.active
            )
        ).formId
    }

    fun update(id: Long, form: FormForm) {
        koki.update(
            id = id,
            request = UpdateFormRequest(
                name = form.name,
                description = form.description,
                active = form.active
            )
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
