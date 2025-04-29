package com.wutsi.koki.portal.client.user.mapper

import com.wutsi.koki.portal.client.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.client.user.model.UserModel
import com.wutsi.koki.tenant.dto.User
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(entity: User, accountId: Long): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            accountId = accountId,
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
}
