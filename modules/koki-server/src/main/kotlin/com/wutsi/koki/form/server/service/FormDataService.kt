package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.FormElementType
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
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
    fun merge(formData: FormDataEntity, newData: Map<String, Any>) {
        val form = formService.get(formData.formId, formData.tenantId)
        val names = formService.extractInputName(form)

        val data = formData.dataAsMap(objectMapper).toMutableMap()
        names.forEach { name ->
            if (newData.containsKey(name)) {
                newData[name]?.let { value -> data[name] = value }
            }
        }
        formData.data = objectMapper.writeValueAsString(data)
        dao.save(formData)
    }

    @Transactional
    fun submit(request: SubmitFormDataRequest, tenantId: Long): FormDataEntity {
        val form = formService.get(request.formId, tenantId)
        if (request.workflowInstanceId != null) {
            val formData = search(
                tenantId = tenantId,
                workflowInstanceIds = listOf(request.workflowInstanceId!!),
                limit = 1
            )
            if (formData.isNotEmpty()) {
                update(formData[0], request.data)
                formSubmissionService.create(formData[0], request)
                return formData[0]
            }
        }

        val now = Date()
        val formData = dao.save(
            FormDataEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                formId = form.id!!,
                workflowInstanceId = request.workflowInstanceId,
                data = objectMapper.writeValueAsString(request.data),
                status = FormDataStatus.SUBMITTED,
                createdAt = now,
                modifiedAt = now,
            )
        )

        formSubmissionService.create(formData, request)

        linkFiles(formData, request.data)
        return formData
    }

    @Transactional
    fun update(formDataId: String, request: UpdateFormDataRequest, tenantId: Long): FormDataEntity {
        val formData = get(formDataId, tenantId)
        update(formData, request.data)
        formSubmissionService.create(formData, request)
        return formData
    }

    @Transactional
    fun linkWithWorkflowInstanceId(id: String, workflowInstanceId: String, tenantId: Long): FormDataEntity {
        val formData = get(id, tenantId)
        if (formData.workflowInstanceId == null) {
            formData.workflowInstanceId = workflowInstanceId
            dao.save(formData)
        } else if (formData.workflowInstanceId != workflowInstanceId) {
            throw IllegalStateException("FormData#$id already associated with a WorkflowInstance")
        }
        return formData
    }

    fun update(formData: FormDataEntity, data: Map<String, Any>): FormDataEntity {
        formData.data = objectMapper.writeValueAsString(data)
        formData.modifiedAt = Date()
        dao.save(formData)

        linkFiles(formData, data)
        return formData
    }

    private fun linkFiles(formData: FormDataEntity, data: Map<String, Any>) {
        formData.workflowInstanceId ?: return

        /* File names */
        val form = formService.get(formData.formId, formData.tenantId)
        val content = objectMapper.readValue(form.content, FormContent::class.java)
        val names = content.elements
            .flatMap { element -> (element.elements ?: emptyList()) }
            .filter { element -> element.type == FormElementType.FILE_UPLOAD }
            .map { element -> element.name }
        if (names.isEmpty()) {
            return
        }

        /* Files */
        val files = fileService.search(
            tenantId = formData.tenantId,
            workflowInstanceIds = listOf(formData.workflowInstanceId!!),
            limit = 100,
        )
        if (files.isEmpty()) {
            return
        }

        val fileIds = names.mapNotNull { name -> data[name] }
        files.forEach { file ->
            if (!fileIds.contains(file.id!!)) {
                file.workflowInstanceId = null
            }
        }
        fileService.save(files)
    }
}
