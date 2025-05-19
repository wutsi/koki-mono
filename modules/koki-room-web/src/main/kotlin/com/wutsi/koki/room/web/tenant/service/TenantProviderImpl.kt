package com.wutsi.koki.room.web.tenant.service

import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.stereotype.Service

@Service
class TenantProviderImpl(private val currentTenantHolder: CurrentTenantHolder) : TenantProvider {
    override fun id(): Long? {
        return currentTenantHolder.get()?.id
    }
}
