package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.dao.TenantRepository
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val dao: TenantRepository
) {
    fun get(id: Long): TenantEntity =
        dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.TENANT_NOT_FOUND)) }
}
