package com.wutsi.koki.contact.server.dao

import com.wutsi.koki.contact.server.domain.ContactTypeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactTypeRepository : CrudRepository<ContactTypeEntity, Long> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): ContactTypeEntity?
}
