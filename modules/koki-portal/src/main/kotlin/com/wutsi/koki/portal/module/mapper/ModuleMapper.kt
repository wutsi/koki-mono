package com.wutsi.koki.portal.module.mapper

import com.wutsi.koki.module.dto.Module
import com.wutsi.koki.module.dto.Permission
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.module.model.PermissionModel
import org.springframework.stereotype.Service

@Service
class ModuleMapper {
    fun toModuleModel(entity: Module, permissions: List<Permission>): ModuleModel {
        return ModuleModel(
            id = entity.id,
            name = entity.name,
            description = entity.description?.trim()?.ifEmpty { null },
            title = entity.title,
            objectType = entity.objectType,
            tabUrl = entity.tabUrl?.trim()?.ifEmpty { null },
            homeUrl = entity.homeUrl?.trim()?.ifEmpty { null },
            settingsUrl = entity.settingsUrl?.trim()?.ifEmpty { null },
            permissions = permissions.map { permission -> toPermissionModel(permission) }
        )
    }

    fun toPermissionModel(entity: Permission): PermissionModel {
        return PermissionModel(
            id = entity.id,
            name = entity.name,
            description = entity.description,
        )
    }
}
