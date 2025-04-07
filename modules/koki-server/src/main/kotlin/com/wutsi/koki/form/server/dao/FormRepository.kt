package com.wutsi.koki.form.server.dao

import com.wutsi.koki.form.server.domain.FormEntity
import org.springframework.data.repository.CrudRepository

interface FormRepository : CrudRepository<FormEntity, Long> {
    fun findByCodeAndTenantId(code: String, tenantId: Long): FormEntity?
}
