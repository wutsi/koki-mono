package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
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
    private val tenantService: TenantService,
    private val roleService: RoleService,
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

    fun getAll(id: List<Long>, tenantId: Long): List<UserEntity> {
        return dao.findByIdInAndTenantId(id, tenantId)
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

        val tenant = tenantService.get(tenantId)
        val salt = UUID.randomUUID().toString()
        val user = dao.save(
            UserEntity(
                email = email,
                displayName = request.displayName,
                status = UserStatus.ACTIVE,
                salt = salt,
                password = passwordService.hash(request.password, salt),
                tenantId = tenant.id!!,
            )
        )

        request.roleIds.forEach { roleId ->
            val role = roleService.get(roleId, tenantId)
            grant(user, role)
        }
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
        user.modifiedAt = Date()
        dao.save(user)
    }

    @Transactional
    fun grant(id: Long, role: RoleEntity, tenantId: Long): Boolean {
        val user = get(id, tenantId)
        return grant(user, role)
    }

    fun grant(user: UserEntity, role: RoleEntity): Boolean {
        if (!user.roles.contains(role)) {
            user.roles.add(role)
            return false
        }
        return false
    }

    @Transactional
    fun revoke(id: Long, role: RoleEntity, tenantId: Long): Boolean {
        val user = get(id, tenantId)
        if (user.roles.contains(role)) {
            user.roles.remove(role)
            return true
        }
        return false
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<UserEntity> {
        val jql = StringBuilder("SELECT U FROM UserEntity U")
        if (roleIds.isNotEmpty()) {
            jql.append(" JOIN U.roles R")
        }

        jql.append(" WHERE U.tenantId = :tenantId")
        if (keyword != null) {
            jql.append(" AND UPPER(U.displayName) LIKE :keyword")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND U.id IN :ids")
        }
        if (roleIds.isNotEmpty()) {
            jql.append(" AND R.id IN :roleIds")
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

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
