package com.wutsi.koki.script.server.dao

import com.wutsi.koki.script.server.domain.ScriptEntity
import org.springframework.data.repository.CrudRepository

interface ScriptRepository : CrudRepository<ScriptEntity, String> {
    fun findByNameIgnoreCaseAndTenantId(name: String, tenantId: Long): ScriptEntity?
}
