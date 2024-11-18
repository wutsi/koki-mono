package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class RoleService(
    private val dao: RoleRepository,
    private val em: EntityManager,
) {
    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RoleEntity> {
        val jql = StringBuilder("SELECT R FROM RoleEntity AS R WHERE R.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND R.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND R.name IN :names")
        }
        if (active != null) {
            jql.append(" AND R.active = :active")
        }
        jql.append(" ORDER BY R.name")

        val query = em.createQuery(jql.toString(), RoleEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun get(id: Long, tenantId: Long): RoleEntity {
        val role = dao.findById(id)
            .orElseThrow { NotFoundException(Error(code = ErrorCode.ROLE_NOT_FOUND)) }

        if (role.tenantId != tenantId) {
            throw NotFoundException(Error(code = ErrorCode.ROLE_NOT_FOUND))
        }
        return role
    }

    fun getAll(ids: List<Long>, tenantId: Long): List<RoleEntity> {
        return search(
            tenantId = tenantId,
            ids = ids,
            limit = ids.size,
        )
    }

    fun getByName(name: String, tenantId: Long): RoleEntity {
        val roles = search(
            names = listOf(name),
            tenantId = tenantId,
            limit = 1
        )
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
