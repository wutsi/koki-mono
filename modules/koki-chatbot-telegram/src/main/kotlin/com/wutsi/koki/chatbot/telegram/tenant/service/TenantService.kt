package com.wutsi.koki.chatbot.telegram.tenant.service

import com.wutsi.koki.chatbot.telegram.tenant.mapper.TenantMapper
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.sdk.KokiTenants
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
) {
    fun tenant(id: Long): TenantModel {
        val tenant = koki.tenant(id).tenant
        return mapper.toTenantModel(tenant)
    }
}
