package com.wutsi.koki.tenant.server.service

interface TenantModuleInitializer {
    fun init(tenantId: Long)
}
