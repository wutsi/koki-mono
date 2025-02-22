package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.module.server.service.PermissionService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
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
    private val securityService: SecurityService,
    private val permissionService: PermissionService,
) {
    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RoleEntity> {
        val jql = StringBuilder("SELECT R FROM RoleEntity AS R WHERE R.tenantId = :tenantId AND R.deleted=false")
        if (ids.isNotEmpty()) {
            jql.append(" AND R.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND UPPER(R.name) IN :names")
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
            query.setParameter("names", names.map { name -> name.uppercase() })
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

        if (role.tenantId != tenantId || role.deleted) {
            throw NotFoundException(Error(code = ErrorCode.ROLE_NOT_FOUND))
        }
        return role
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
    fun create(request: CreateRoleRequest, tenantId: Long): RoleEntity {
        val duplicate = search(
            tenantId = tenantId,
            names = listOf(request.name),
            limit = 1
        )
        if (duplicate.isNotEmpty()) {
            throw ConflictException(error = Error(ErrorCode.ROLE_DUPLICATE_NAME))
        }

        val userId = securityService.getCurrentUserId()
        val role = dao.save(
            RoleEntity(
                tenantId = tenantId,
                name = request.name,
                title = request.title,
                description = request.description,
                active = request.active,
                modifiedById = userId,
                createdById = userId,
            )
        )
        setPermissions(role, request.permissionIds)
        return role
    }

    @Transactional
    fun update(id: Long, request: UpdateRoleRequest, tenantId: Long) {
        val duplicate = search(
            tenantId = tenantId,
            names = listOf(request.name),
        )
        if (duplicate.isNotEmpty() && duplicate[0].id != id) {
            throw ConflictException(error = Error(ErrorCode.ROLE_DUPLICATE_NAME))
        }

        val role = get(id, tenantId)
        role.name = request.name
        role.title = request.title
        role.description = request.description
        role.active = request.active
        role.modifiedAt = Date()
        role.modifiedById = securityService.getCurrentUserId()
        dao.save(role)

        setPermissions(role, request.permissionIds)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val role = get(id, tenantId)
        role.deleted = true
        role.deletedAt = Date()
        role.deletedById = securityService.getCurrentUserId()
        dao.save(role)
    }

    fun setPermissions(role: RoleEntity, permissionIds: List<Long>) {
        if (permissionIds.isEmpty()) {
            role.permissions.clear()
        } else {
            role.permissions = permissionService.search(
                ids = permissionIds,
                limit = permissionIds.size
            ).toMutableList()
        }
        role.modifiedById = securityService.getCurrentUserId()
        dao.save(role)
    }

    @Transactional
    open fun save(role: RoleEntity): RoleEntity {
        role.modifiedAt = Date()
        return dao.save(role)
    }
}
