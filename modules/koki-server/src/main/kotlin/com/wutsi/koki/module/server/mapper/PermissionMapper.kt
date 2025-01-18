package com.wutsi.koki.module.server.mapper

import com.wutsi.koki.module.dto.Module
import com.wutsi.koki.module.dto.ModuleSummary
import com.wutsi.koki.module.server.domain.ModuleEntity
import org.springframework.stereotype.Service

@Service
class ModuleMapper {
    fun toModule(entity: ModuleEntity): Module {
        return Module(
            id = entity.id,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            homeUrl = entity.homeUrl,
            tabUrl = entity.tabUrl,
            settingsUrl = entity.settingsUrl,
        )
    }

    fun toModuleSummary(entity: ModuleEntity): ModuleSummary {
        return ModuleSummary(
            id = entity.id,
            name = entity.name,
            title = entity.title,
        )
    }
}
