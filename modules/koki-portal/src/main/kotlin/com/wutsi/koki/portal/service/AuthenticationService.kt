package com.wutsi.koki.portal.rest

import com.wutsi.koki.sdk.TenantProvider
import org.springframework.stereotype.Service

@Service
class TenantService : TenantProvider {
    override fun id(): Long = 1
}
