package com.wutsi.koki.portal.client.module.mapper

import com.wutsi.koki.module.dto.Module
import com.wutsi.koki.portal.client.module.model.ModuleModel
import org.springframework.stereotype.Service

@Service
class ModuleMapper {
    fun toModuleModel(entity: Module): ModuleModel {
        return ModuleModel(
            id = entity.id,
            name = entity.name,
            description = entity.description?.trim()?.ifEmpty { null },
            title = entity.title,
            objectType = entity.objectType,
            tabUrl = entity.tabUrl?.trim()?.ifEmpty { null },
            homeUrl = entity.homeUrl?.trim()?.ifEmpty { null },
            settingsUrl = entity.settingsUrl?.trim()?.ifEmpty { null },
            jsUrl = entity.jsUrl?.trim()?.ifEmpty { null },
            cssUrl = entity.cssUrl?.trim()?.ifEmpty { null },
        )
    }
}
