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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class UserService(
    private val dao: UserRepository,
    private val passwordService: PasswordService,
    private val tenantService: TenantService,
) {
    companion object {
        const val PAGE_SIZE = 20
    }

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
        val user = dao.findByEmailAndTenantId(email, tenantId)
        if (user != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.USER_DUPLICATE_EMAIL
                )
            )
        }

        val tenant = tenantService.get(tenantId)
        val salt = UUID.randomUUID().toString()
        return dao.save(
            UserEntity(
                email = email,
                displayName = request.displayName,
                status = UserStatus.ACTIVE,
                salt = salt,
                password = passwordService.hash(request.password, salt),
                tenantId = tenant.id!!,
            )
        )
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
        keyword: String,
        ids: List<Long>,
        tenantId: Long,
        limit: Int,
        offset: Int,
        sortBy: String? = null,
        ascending: Boolean = true,
    ): List<UserEntity> {
        if (ids.isNotEmpty()) {
            val pageable = createPageable(ids.size, offset, sortBy, ascending)
            return dao.findByIdInAndTenantId(ids, tenantId, pageable).toList()
        } else if (keyword.isNotEmpty()) {
            val pageable = createPageable(limit, offset, sortBy, ascending)
            return dao.findByDisplayNameLikeIgnoreCaseAndTenantId("%$keyword%", tenantId, pageable)
        } else {
            val pageable = createPageable(limit, offset, sortBy, ascending)
            return dao.findByTenantId(tenantId, pageable)
        }
    }

    private fun createPageable(limit: Int, offset: Int, sortBy: String?, ascending: Boolean): Pageable {
        val direction = if (ascending) Sort.Direction.ASC else Sort.Direction.DESC
        val property = sortBy ?: "displayName"
        val pageSize = if (limit <= 0) PAGE_SIZE else limit

        return PageRequest.of(offset / pageSize, pageSize, direction, property)
    }
}
