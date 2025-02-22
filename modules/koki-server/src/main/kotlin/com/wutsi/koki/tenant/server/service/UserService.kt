package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.domain.UserEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class UserService(
    private val dao: UserRepository,
    private val passwordService: PasswordService,
    private val roleService: RoleService,
    private val securityService: SecurityService,
    private val em: EntityManager
) {
    fun get(id: Long, tenantId: Long): UserEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.USER_NOT_FOUND)) }

        if (user.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
        }
        return user
    }

    fun getByEmail(email: String, tenantId: Long): UserEntity {
        return dao.findByEmailAndTenantId(email, tenantId)
            ?: throw NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
    }

    @Transactional
    fun create(request: CreateUserRequest, tenantId: Long): UserEntity {
        val email = request.email.lowercase()
        val duplicate = dao.findByEmailAndTenantId(email, tenantId)
        if (duplicate != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_DUPLICATE_EMAIL
                )
            )
        }

        val salt = UUID.randomUUID().toString()
        val currentUserId = securityService.getCurrentUserIdOrNull()
        val user = dao.save(
            UserEntity(
                tenantId = tenantId,
                email = email,
                displayName = request.displayName,
                status = UserStatus.ACTIVE,
                salt = salt,
                password = passwordService.hash(request.password, salt),
                createdById = currentUserId,
                modifiedById = currentUserId,
            )
        )

        setRoles(user, request.roleIds.distinct())
        return user
    }

    @Transactional
    fun update(userId: Long, request: UpdateUserRequest, tenantId: Long) {
        val email = request.email.lowercase()
        val duplicate = dao.findByEmailAndTenantId(email, tenantId)
        if (duplicate != null && duplicate.id != userId) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_DUPLICATE_EMAIL
                )
            )
        }

        val user = get(userId, tenantId)
        user.email = email
        user.displayName = request.displayName
        request.status?.let { status -> user.status = status }
        user.modifiedById = securityService.getCurrentUserIdOrNull()
        user.modifiedAt = Date()
        dao.save(user)

        setRoles(user, request.roleIds.distinct())
    }

    private fun setRoles(user: UserEntity, roleIds: List<Long>) {
        if (roleIds.isEmpty()) {
            user.roles.clear()
        } else {
            user.roles = roleService.search(
                tenantId = user.tenantId,
                ids = roleIds,
                limit = roleIds.size,
            ).toMutableList()
        }
        user.modifiedById = securityService.getCurrentUserIdOrNull()
        user.modifiedAt = Date()
        dao.save(user)
    }

    fun setType(user: UserEntity, type: UserType): UserEntity {
        if (user.type == UserType.UNKNOWN) {
            user.type = type
            return dao.save(user)
        } else {
            throw ConflictException(
                error = Error(code = ErrorCode.USER_ALREADY_ASSIGNED)
            )
        }
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        status: UserStatus? = null,
        type: UserType? = null,
        permissions: List<String> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserEntity> {
        val jql = StringBuilder("SELECT U FROM UserEntity U")
        if (roleIds.isNotEmpty() || permissions.isNotEmpty()) {
            jql.append(" JOIN U.roles R")
        }
        if (permissions.isNotEmpty()) {
            jql.append(" JOIN R.permissions P")
        }

        jql.append(" WHERE U.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND UPPER(U.displayName) LIKE :keyword")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND U.id IN :ids")
        }
        if (status != null) {
            jql.append(" AND U.status = :status")
        }
        if (type != null) {
            jql.append(" AND U.type = :type")
        }
        if (roleIds.isNotEmpty()) {
            jql.append(" AND R.id IN :roleIds")
        }
        if (permissions.isNotEmpty()) {
            jql.append(" AND P.name IN :permissions")
        }
        jql.append(" ORDER BY U.displayName")

        val query = em.createQuery(jql.toString(), UserEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (roleIds.isNotEmpty()) {
            query.setParameter("roleIds", roleIds)
        }
        if (status != null) {
            query.setParameter("status", status)
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (permissions.isNotEmpty()) {
            query.setParameter("permissions", permissions)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
