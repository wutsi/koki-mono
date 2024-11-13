package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ForbiddenException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormDataService(
    private val dao: FormDataRepository,
    private val formService: FormService,
    private val objectMapper: ObjectMapper,
    private val securityService: SecurityService,
) {
    fun get(id: String, form: FormEntity): FormDataEntity {
        val formData = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND)) }

        if (formData.form.id != form.id) {
            throw NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND))
        }
        return formData
    }

    fun get(id: String, tenantId: Long): FormDataEntity {
        val formData = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND)) }

        if (formData.tenant.id != tenantId) {
            throw NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND))
        }
        return formData
    }

    @Transactional
    fun submit(request: SubmitFormDataRequest, tenantId: Long): FormDataEntity {
        val form = formService.get(request.formId, tenantId)
        val now = Date()
        val formData = FormDataEntity(
            id = UUID.randomUUID().toString(),
            tenant = form.tenant,
            form = form,
            workflowInstanceId = request.workflowInstanceId,
            activityInstanceId = request.activityInstanceId,
            data = objectMapper.writeValueAsString(request.data),
            status = FormDataStatus.SUBMITTED,
            userId = securityService.getCurrentUserId(),
            createdAt = now,
            modifiedAt = now,
        )
        return dao.save(formData)
    }

    @Transactional
    fun update(formDataId: String, request: UpdateFormDataRequest, tenantId: Long): FormDataEntity {
        val formData = get(formDataId, tenantId)

        // Check access
        if (formData.userId != securityService.getCurrentUserId()) {
            throw ForbiddenException(
                error = Error(
                    ErrorCode.AUTHORIZATION_PERMISSION_DENIED
                )
            )
        }

        // Update
        formData.data = objectMapper.writeValueAsString(request.data)
        formData.modifiedAt = Date()
        return dao.save(formData)
    }
}
