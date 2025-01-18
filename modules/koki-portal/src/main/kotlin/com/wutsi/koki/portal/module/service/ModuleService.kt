package com.wutsi.koki.portal.module.service

import com.wutsi.koki.portal.module.mapper.ModuleMapper
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.sdk.KokiModules
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ModuleService(
    private val koki: KokiModules,
    private val mapper: ModuleMapper
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ModuleService::class.java)
    }

    private var all: List<ModuleModel>? = null

    fun modules(): List<ModuleModel> {
        if (all == null) {
            val modules = koki.modules().modules
            LOGGER.info("${all?.size} Modules(s) loaded")

            val permissions = koki.permissions(
                moduleIds = modules.map { module -> module.id },
                limit = Integer.MAX_VALUE
            )
                .permissions
                .groupBy { permission -> permission.moduleId }

            all = modules.map { module ->
                mapper.toModuleModel(
                    entity = module,
                    permissions = permissions[module.id] ?: emptyList()
                )
            }
        }
        return all!!
    }
}
