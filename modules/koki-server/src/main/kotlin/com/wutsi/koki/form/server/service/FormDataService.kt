package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.platform.util.MapUtils
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormDataService(
    private val dao: FormDataRepository,
    private val formService: FormService,
    private val fileService: FileService,
    private val formSubmissionService: FormSubmissionService,
    private val objectMapper: ObjectMapper,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): FormDataEntity {
        val formData = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND)) }

        if (formData.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND))
        }
        return formData
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        formIds: List<String> = emptyList(),
        workflowInstanceIds: List<String> = emptyList(),
        status: FormDataStatus? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FormDataEntity> {
        val jql = StringBuilder("SELECT F FROM FormDataEntity F")
        jql.append(" WHERE F.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (formIds.isNotEmpty()) {
            jql.append(" AND F.formId IN :formIds")
        }
        if (workflowInstanceIds.isNotEmpty()) {
            jql.append(" AND F.workflowInstanceId IN :workflowInstanceIds")
        }
        if (status != null) {
            jql.append(" AND F.status IN :status")
        }

        val query = em.createQuery(jql.toString(), FormDataEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (formIds.isNotEmpty()) {
            query.setParameter("formIds", formIds)
        }
        if (workflowInstanceIds.isNotEmpty()) {
            query.setParameter("workflowInstanceIds", workflowInstanceIds)
        }
        if (status != null) {
            query.setParameter("status", status)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun submit(request: SubmitFormDataRequest, tenantId: Long): FormDataEntity {
        val form = formService.get(request.formId, tenantId)
        var formData: FormDataEntity? = null
        if (request.workflowInstanceId != null) {
            formData = search(
                tenantId = tenantId,
                workflowInstanceIds = listOf(request.workflowInstanceId!!),
                limit = 1
            ).firstOrNull()
            if (formData != null) {
                merge(formData, request.data)
            }
        }

        val now = Date()
        if (formData == null) {
            formData = dao.save(
                FormDataEntity(
                    id = UUID.randomUUID().toString(),
                    tenantId = tenantId,
                    formId = form.id!!,
                    workflowInstanceId = request.workflowInstanceId,
                    data = MapUtils.toJsonString(request.data, objectMapper),
                    status = FormDataStatus.SUBMITTED,
                    createdAt = now,
                    modifiedAt = now,
                )
            )
        }

        linkFiles(formData)
        formSubmissionService.create(formData, request)
        return formData
    }

    @Transactional
    fun update(formDataId: String, request: UpdateFormDataRequest, tenantId: Long): FormDataEntity {
        val formData = get(formDataId, tenantId)
        merge(formData, request.data)

        formSubmissionService.create(formData, request)
        return formData
    }

    fun merge(formData: FormDataEntity, newData: Map<String, Any>) {
        val form = formService.get(formData.formId, formData.tenantId)
        val names = formService.extractInputName(form)

        val data = formData.dataAsMap(objectMapper).toMutableMap()
        names.forEach { name ->
            if (newData.containsKey(name)) {
                newData[name]?.let { value -> data[name] = value }
            }
        }
        formData.data = MapUtils.toJsonString(data, objectMapper)
        formData.modifiedAt = Date()
        dao.save(formData)
    }

    @Transactional
    fun linkWithWorkflowInstanceId(id: String, workflowInstanceId: String, tenantId: Long): FormDataEntity {
        val formData = get(id, tenantId)
        if (formData.workflowInstanceId == null) {
            formData.workflowInstanceId = workflowInstanceId
            linkFiles(formData)
            dao.save(formData)
        } else if (formData.workflowInstanceId != workflowInstanceId) {
            throw IllegalStateException("FormData#$id already associated with a WorkflowInstance")
        }
        return formData
    }

    private fun linkFiles(formData: FormDataEntity) {
        formData.workflowInstanceId ?: return

        val form = formService.get(formData.formId, formData.tenantId)
        val names = formService.extractInputName(form, FormElementType.FILE_UPLOAD)
        if (names.isEmpty()) {
            return
        }

        val data = formData.dataAsMap(objectMapper)
        val fileIds = names.mapNotNull { name -> data[name]?.toString() }
        fileService.link(fileIds, formData.workflowInstanceId!!, formData.tenantId)
    }
}
