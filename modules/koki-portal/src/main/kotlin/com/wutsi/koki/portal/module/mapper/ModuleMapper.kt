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
            description = entity.description,
            title = entity.title,
            objectType = entity.objectType,
            tabUrl = entity.tabUrl,
            homeUrl = entity.homeUrl,
            settingsUrl = entity.settingsUrl,
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
