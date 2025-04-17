package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.account.dto.UpdateAccountUserRequest
import com.wutsi.koki.account.server.dao.AccountUserRepository
import com.wutsi.koki.account.server.domain.AccountUserEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.service.PasswordService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class AccountUserService(
    private val dao: AccountUserRepository,
    private val passwordService: PasswordService,
    private val accountService: AccountService,
) {
    fun get(id: Long, tenantId: Long): AccountUserEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.USER_NOT_FOUND)) }

        if (user.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
        }
        return user
    }

    fun getByUsernameOrNull(username: String, tenantId: Long): AccountUserEntity? {
        return dao.findByUsernameAndTenantId(username.lowercase(), tenantId)
    }

    @Transactional
    fun create(request: CreateAccountUserRequest, tenantId: Long): AccountUserEntity {
        val duplicate = getByUsernameOrNull(request.username, tenantId)
        if (duplicate != null) {
            throw ConflictException(Error(ErrorCode.USER_DUPLICATE_USERNAME))
        }

        val account = accountService.get(request.accountId, tenantId)
        val salt = UUID.randomUUID().toString()
        val user = dao.save(
            AccountUserEntity(
                tenantId = tenantId,
                accountId = request.accountId,
                username = request.username.lowercase(),
                salt = salt,
                password = passwordService.hash(request.password, salt),
                status = request.status,
            )
        )

        accountService.setUser(account, user)
        return user
    }

    @Transactional
    fun update(id: Long, request: UpdateAccountUserRequest, tenantId: Long): AccountUserEntity {
        val duplicate = getByUsernameOrNull(request.username, tenantId)
        if (duplicate != null && duplicate.id != id) {
            throw ConflictException(Error(ErrorCode.USER_DUPLICATE_USERNAME))
        }

        val user = get(id, tenantId)
        user.username = request.username.lowercase()
        user.status = request.status
        user.modifiedAt = Date()
        return dao.save(user)
    }
}
