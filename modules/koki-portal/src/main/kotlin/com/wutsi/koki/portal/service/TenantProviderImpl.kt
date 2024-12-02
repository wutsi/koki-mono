package com.wutsi.koki.portal.service

import com.wutsi.koki.sdk.TenantProvider
import org.springframework.stereotype.Service

@Service
class TenantProviderImpl : TenantProvider {
    override fun id() = 1L
}
