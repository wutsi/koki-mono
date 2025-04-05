package com.wutsi.koki.translation.server.service

import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.springframework.stereotype.Service

@Service
class TenantTranslationInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.TRANSLATION_PROVIDER,
            value = TranslationProvider.AWS.name,
            tenantId = tenantId,
        )
    }
}
