package com.wutsi.koki.file.server.service

import com.wutsi.koki.platform.storage.StorageType
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.AbstractTenantModuleInitializer
import org.springframework.stereotype.Service

@Service
class TenantFileInitializer : AbstractTenantModuleInitializer() {
    override fun init(tenantId: Long) {
        setConfigurationIfMissing(
            name = ConfigurationName.STORAGE_TYPE,
            value = StorageType.KOKI.name,
            tenantId = tenantId,
        )
    }
}
