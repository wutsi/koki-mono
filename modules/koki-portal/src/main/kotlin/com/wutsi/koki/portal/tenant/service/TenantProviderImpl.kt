package com.wutsi.koki.portal.tenant.service

import com.wutsi.koki.sdk.TenantProvider
import org.springframework.stereotype.Service

@Service
class TenantProviderImpl(private val currentTenantHolder: CurrentTenantHolder) : TenantProvider {
    override fun id(): Long {
        return currentTenantHolder.get()?.id ?: -1
    }
}
