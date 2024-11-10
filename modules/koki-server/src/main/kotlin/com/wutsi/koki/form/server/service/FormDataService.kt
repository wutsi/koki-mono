package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.form.server.domain.FormEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FormDataService(
    private val dao: FormDataRepository,
    private val objectMapper: ObjectMapper,
) {
    fun get(id: String, tenantId: Long): FormDataEntity {
        val formData = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND)) }

        if (formData.tenant.id != tenantId) {
            throw NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND))
        }
        return formData
    }

    fun get(id: String, form: FormEntity): FormDataEntity {
        val formData = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND)) }

        if (formData.form.id != form.id) {
            throw NotFoundException(Error(ErrorCode.FORM_DATA_NOT_FOUND))
        }
        return formData
    }

    @Transactional
    fun save(data: Map<String, String>, form: FormEntity): FormDataEntity {
        val now = Date()
        return dao.save(
            FormDataEntity(
                id = UUID.randomUUID().toString(),
                tenant = form.tenant,
                form = form,
                data = objectMapper.writeValueAsString(data),
                createdAt = now,
                modifiedAt = now
            )
        )
    }

    @Transactional
    fun save(data: Map<String, String>, formData: FormDataEntity): FormDataEntity {
        formData.data = objectMapper.writeValueAsString(data)
        formData.modifiedAt = Date()
        return dao.save(formData)
    }
}
