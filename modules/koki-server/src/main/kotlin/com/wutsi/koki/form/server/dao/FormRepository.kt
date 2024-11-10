package com.wutsi.koki.form.server.dao

import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.data.repository.CrudRepository

interface FormRepository : CrudRepository<FormEntity, String> {
    fun findByNameAndTenant(name: String, tenant: TenantEntity): FormEntity?
}
