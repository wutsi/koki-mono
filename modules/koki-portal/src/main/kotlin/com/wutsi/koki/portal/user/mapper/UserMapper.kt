package com.wutsi.koki.portal.user.mapper

import com.wutsi.koki.portal.mapper.TenantAwareMapper
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service
import java.util.Locale
import kotlin.collections.flatMap

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(
        entity: User,
        roles: List<RoleModel>,
        category: CategoryModel?,
        city: LocationModel?,
    ): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            roles = roles,
            employer = entity.employer,
            photoUrl = entity.photoUrl,
            mobile = entity.mobile,
            category = category,
            city = city,
            permissionNames = roles.flatMap { role -> role.permissions }
                .distinctBy { permission -> permission.id }
                .map { permission -> permission.name }
        )
    }

    fun toUserModel(entity: UserSummary): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            employer = entity.employer,
            photoUrl = entity.photoUrl,
            mobile = entity.mobile,
        )
    }
}
