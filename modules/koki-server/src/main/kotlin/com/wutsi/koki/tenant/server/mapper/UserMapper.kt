package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUser(entity: UserEntity) = User(
        id = entity.id!!,
        accountId = entity.accountId,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        type = entity.type,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
        roleIds = entity.roles.mapNotNull { role -> role.id },
        language = entity.language,
    )

    fun toUserSummary(entity: UserEntity) = UserSummary(
        id = entity.id!!,
        accountId = entity.accountId,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        type = entity.type,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
    )
}
