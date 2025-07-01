package com.wutsi.koki.chatbot.messenger.service

import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TenantProviderImpl(
    @Value("\${koki.webapp.tenant-id}") private val tenantId: Long
) : TenantProvider {
    override fun id(): Long {
        return tenantId
    }
}
