package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toUser(entity: UserEntity) = User(
        id = entity.id!!,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        language = entity.language,
        employer = entity.employer,
        mobile = entity.mobile,
        country = entity.country,
        photoUrl = entity.photoUrl,
        categoryId = entity.categoryId,
        cityId = entity.cityId,
        roleIds = entity.roles.mapNotNull { role -> role.id },
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
    )

    fun toUserSummary(entity: UserEntity) = UserSummary(
        id = entity.id!!,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        photoUrl = entity.photoUrl,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
    )
}
