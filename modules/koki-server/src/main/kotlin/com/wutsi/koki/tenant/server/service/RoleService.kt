package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class RoleService(
    private val dao: RoleRepository
) {
    fun search(tenantId: Long, names: List<String> = emptyList()): List<RoleEntity> {
        return if (names.isEmpty()) {
            dao.findByTenantId(tenantId)
        } else {
            dao.findByTenantIdAndNameIn(tenantId, names)
        }
    }

    fun findByName(tenantId: Long, name: String): RoleEntity {
        val roles = search(tenantId, listOf(name))
        if (roles.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.ROLE_NOT_FOUND,
                    parameter = Parameter(
                        value = name
                    )
                )
            )
        } else {
            return roles.first()
        }
    }

    @Transactional
    open fun save(role: RoleEntity): RoleEntity {
        role.modifiedAt = Date()
        return dao.save(role)
    }
}
