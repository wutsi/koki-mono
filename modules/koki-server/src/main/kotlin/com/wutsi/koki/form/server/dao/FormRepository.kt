package com.wutsi.koki.form.server.dao

import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface FormRepository : CrudRepository<FormEntity, String> {
    @Query("SELECT F FROM FormEntity F WHERE F.name=?1 AND F.tenant.id=?2")
    fun findByNameAndTenantId(name: String, tenantId: Long): FormEntity?

    fun findByNameAndTenant(name: String, tenant: TenantEntity): FormEntity?
}
