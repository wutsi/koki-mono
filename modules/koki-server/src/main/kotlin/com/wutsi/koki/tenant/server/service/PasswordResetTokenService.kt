package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.ResetPasswordRequest
import com.wutsi.koki.tenant.dto.SendPasswordRequest
import com.wutsi.koki.tenant.server.command.SendPasswordCommand
import com.wutsi.koki.tenant.server.dao.PasswordResetTokenRepository
import com.wutsi.koki.tenant.server.domain.PasswordResetTokenEntity
import jakarta.transaction.Transactional
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class PasswordResetTokenService(
    private val dao: PasswordResetTokenRepository,
    private val userService: UserService,
    private val publisher: Publisher,
) {
    fun get(id: String, tenantId: Long): PasswordResetTokenEntity {
        val token = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND)) }

        if (token.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND))
        }
        return token
    }

    @Transactional
    fun send(request: SendPasswordRequest, tenantId: Long): PasswordResetTokenEntity {
        val user = userService.getByEmail(request.email, tenantId)

        val now = Date()
        val token = dao.save(
            PasswordResetTokenEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                user = user,
                createdAt = now,
                expiresAt = DateUtils.addDays(now, 1),
            )
        )

        publisher.publish(
            SendPasswordCommand(
                tokenId = token.id ?: "",
                tenantId = user.tenantId,
            )
        )
        return token
    }

    @Transactional
    fun reset(request: ResetPasswordRequest, tenantId: Long) {
        val now = Date()
        val token = get(request.tokenId, tenantId)
        if (token.expiresAt.before(now)) {
            throw ConflictException(Error(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED))
        }

        // Update the password
        userService.updatePassword(token.user, request.password)

        // Expire the token
        token.expiresAt = Date(now.time - 1)
        dao.save(token)
    }
}
