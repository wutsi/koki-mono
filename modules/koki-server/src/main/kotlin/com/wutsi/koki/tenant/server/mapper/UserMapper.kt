package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ProfileStrengthCalculator
import org.springframework.stereotype.Service

@Service
class UserMapper(
    private val profileStrengthCalculator: ProfileStrengthCalculator,
) {
    fun toUser(entity: UserEntity) = User(
        id = entity.id!!,
        deviceId = entity.deviceId,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        language = entity.language?.ifEmpty { null },
        employer = entity.employer?.ifEmpty { null },
        mobile = entity.mobile?.ifEmpty { null },
        country = entity.country?.ifEmpty { null },
        photoUrl = entity.photoUrl?.ifEmpty { null },
        categoryId = entity.categoryId,
        cityId = entity.cityId,
        roleIds = entity.roles.mapNotNull { role -> role.id },
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
        invitationId = entity.invitationId?.ifEmpty { null },
        websiteUrl = entity.websiteUrl?.ifEmpty { null },
        twitterUrl = entity.twitterUrl?.ifEmpty { null },
        facebookUrl = entity.facebookUrl?.ifEmpty { null },
        instagramUrl = entity.instagramUrl?.ifEmpty { null },
        biography = entity.biography?.ifEmpty { null },
        youtubeUrl = entity.youtubeUrl?.ifEmpty { null },
        tiktokUrl = entity.tiktokUrl?.ifEmpty { null },
        profileStrength = profileStrengthCalculator.calculate(entity),
        street = entity.street,
    )

    fun toUserSummary(entity: UserEntity) = UserSummary(
        id = entity.id!!,
        displayName = entity.displayName,
        username = entity.username,
        email = entity.email,
        status = entity.status,
        employer = entity.employer,
        mobile = entity.mobile,
        photoUrl = entity.photoUrl,
        createdAt = entity.createdAt,
        modifiedAt = entity.modifiedAt,
        cityId = entity.cityId,
        country = entity.country,
    )
}
