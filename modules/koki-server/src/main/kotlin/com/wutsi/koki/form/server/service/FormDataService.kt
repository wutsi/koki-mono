package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.domain.FormEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormDataService(
    private val dao: FormDataRepository,
    private val formService: FormService,
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
        limit: Int = 200,
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

        if (request.workflowInstanceId != null) {
            val formData = search(
                tenantId = tenantId,
                workflowInstanceIds = listOf(request.workflowInstanceId!!),
                limit = 1
            )
            if (formData.isNotEmpty()) {
                return update(formData[0], request.data)
            }
        }

        val now = Date()
        val formData = FormDataEntity(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            formId = form.id!!,
            workflowInstanceId = request.workflowInstanceId,
            data = objectMapper.writeValueAsString(request.data),
            status = FormDataStatus.SUBMITTED,
            createdAt = now,
            modifiedAt = now,
        )
        return dao.save(formData)
    }

    @Transactional
    fun update(formDataId: String, request: UpdateFormDataRequest, tenantId: Long): FormDataEntity {
        val formData = get(formDataId, tenantId)
        return update(formData, request.data)
    }

    fun update(formData: FormDataEntity, data: Map<String, Any>): FormDataEntity {
        formData.data = objectMapper.writeValueAsString(data)
        formData.modifiedAt = Date()
        return dao.save(formData)
    }
}
