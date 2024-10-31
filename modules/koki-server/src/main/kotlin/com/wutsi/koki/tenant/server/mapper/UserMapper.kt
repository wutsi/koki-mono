package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUser(entity: UserEntity) = User(
        id = entity.id ?: -1,
        displayName = entity.displayName,
        email = entity.email,
        status = entity.status,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
    )
}
