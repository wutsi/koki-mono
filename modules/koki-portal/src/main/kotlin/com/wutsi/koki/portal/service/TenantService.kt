package com.wutsi.koki.portal.service

import com.wutsi.koki.sdk.KokiTenant
import com.wutsi.koki.sdk.TenantProvider
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.stereotype.Service

@Service
class TenantService(private val koki: KokiTenant) {
    fun configurations(
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): Map<String, String> {
        return koki.configurations(
            names = names,
            keyword = keyword,
        ).configurations
            .map { config -> config.name to config.value }
            .toMap() as Map<String, String>
    }

    fun save(values: Map<String, String>) {
        koki.save(SaveConfigurationRequest(values))
    }
}
