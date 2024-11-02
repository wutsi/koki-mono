package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class RoleService(
    private val dao: RoleRepository
) {
    fun search(names: List<String> = emptyList(), tenantId: Long): List<RoleEntity> {
        return if (names.isEmpty()) {
            dao.findByTenantId(tenantId)
        } else {
            dao.findByTenantIdAndNameIn(tenantId, names)
        }
    }

    fun get(id: Long, tenantId: Long): RoleEntity {
        val role = dao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.ROLE_NOT_FOUND)) }

        if (role.tenant.id != tenantId) {
            throw NotFoundException(Error(code = ErrorCode.ROLE_NOT_FOUND))
        }
        return role
    }

    fun getAll(id: List<Long>, tenantId: Long): List<RoleEntity> {
        return dao.findByIdInAndTenantId(id, tenantId)
    }

    fun getByName(name: String, tenantId: Long): RoleEntity {
        val roles = search(listOf(name), tenantId)
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
