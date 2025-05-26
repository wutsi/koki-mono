package com.wutsi.koki.room.web.user.mapper

import com.wutsi.koki.room.web.common.mapper.TenantAwareMapper
import com.wutsi.koki.room.web.user.model.UserModel
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(entity: User): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            accountId = entity.accountId,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            type = entity.type,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toUserModel(entity: UserSummary): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            accountId = entity.accountId,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            type = entity.type,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }
}
