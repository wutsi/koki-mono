package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.tenant.server.dao.TenantRepository
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val dao: TenantRepository
) {
    fun get(id: Long): TenantEntity =
        dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.TENANT_NOT_FOUND)) }
}
