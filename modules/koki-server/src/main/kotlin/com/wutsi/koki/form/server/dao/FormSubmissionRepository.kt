package com.wutsi.koki.form.server.dao

import com.wutsi.koki.form.server.domain.FormSubmissionEntity
import org.springframework.data.repository.CrudRepository

interface FormSubmissionRepository : CrudRepository<FormSubmissionEntity, String> {
    fun findByFormId(formId: String): List<FormSubmissionEntity>
}
