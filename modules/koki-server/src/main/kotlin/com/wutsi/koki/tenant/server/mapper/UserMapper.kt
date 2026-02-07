package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ProfileStrengthCalculator
import com.wutsi.koki.tenant.server.service.UserImageResizer
import org.springframework.stereotype.Service

@Service
class UserMapper(
    private val profileStrengthCalculator: ProfileStrengthCalculator,
    private val imageResizer: UserImageResizer,
) {
    fun toUser(entity: UserEntity): User {
        val photoUrl = entity.photoUrl?.ifEmpty { null }
        return User(
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
            photoUrl = photoUrl,
            photoTinyUrl = photoUrl?.let { url -> imageResizer.tinyUrl(url) },
            photoThumbnailUrl = photoUrl?.let { url -> imageResizer.thumbnailUrl(url) },
            photoPreviewUrl = photoUrl?.let { url -> imageResizer.previewUrl(url) },
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
    }

    fun toUserSummary(entity: UserEntity): UserSummary {
        val photoUrl = entity.photoUrl?.ifEmpty { null }
        return UserSummary(
            id = entity.id!!,
            displayName = entity.displayName,
            username = entity.username,
            email = entity.email,
            status = entity.status,
            employer = entity.employer,
            mobile = entity.mobile,
            photoUrl = photoUrl,
            photoTinyUrl = photoUrl?.let { url -> imageResizer.tinyUrl(url) },
            photoThumbnailUrl = photoUrl?.let { url -> imageResizer.thumbnailUrl(url) },
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            cityId = entity.cityId,
            country = entity.country,
        )
    }
}
