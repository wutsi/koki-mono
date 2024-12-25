package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormSubmissionRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.domain.FormSubmissionEntity
import com.wutsi.koki.platform.util.MapUtils
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormSubmissionService(
    private val dao: FormSubmissionRepository,
    private val securityService: SecurityService,
    private val objectMapper: ObjectMapper,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): FormSubmissionEntity {
        val submission = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_SUBMISSION_NOT_FOUND)) }

        if (submission.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.FORM_SUBMISSION_NOT_FOUND))
        }
        return submission
    }

    fun search(
        tenantId: Long,
        formIds: List<String> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<FormSubmissionEntity> {
        val jql = StringBuilder("SELECT F FROM FormSubmissionEntity F")
        jql.append(" WHERE F.tenantId = :tenantId")
        if (formIds.isNotEmpty()) {
            jql.append(" AND F.formId IN :formIds")
        }
        jql.append(" ORDER BY F.submittedAt DESC")

        val query = em.createQuery(jql.toString(), FormSubmissionEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (formIds.isNotEmpty()) {
            query.setParameter("formIds", formIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(formData: FormDataEntity, request: SubmitFormDataRequest): FormSubmissionEntity {
        return dao.save(
            FormSubmissionEntity(
                id = UUID.randomUUID().toString(),
                tenantId = formData.tenantId,
                formId = formData.formId,
                workflowInstanceId = request.workflowInstanceId,
                activityInstanceId = request.activityInstanceId,
                data = MapUtils.toJsonString(request.data, objectMapper),
                submittedAt = Date(),
                submittedById = securityService.getCurrentUserIdOrNull(),
            )
        )
    }

    @Transactional
    fun create(formData: FormDataEntity, request: UpdateFormDataRequest): FormSubmissionEntity {
        return dao.save(
            FormSubmissionEntity(
                id = UUID.randomUUID().toString(),
                tenantId = formData.tenantId,
                formId = formData.formId,
                workflowInstanceId = formData.workflowInstanceId,
                activityInstanceId = request.activityInstanceId,
                data = MapUtils.toJsonString(request.data, objectMapper),
                submittedAt = Date(),
                submittedById = securityService.getCurrentUserIdOrNull(),
            )
        )
    }
}
