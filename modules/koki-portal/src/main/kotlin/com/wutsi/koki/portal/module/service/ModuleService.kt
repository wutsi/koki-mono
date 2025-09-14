package com.wutsi.koki.portal.module.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.common.service.TogglesHolder
import com.wutsi.koki.portal.module.mapper.ModuleMapper
import com.wutsi.koki.portal.module.model.LayoutDescriptor
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.module.model.PermissionModel
import com.wutsi.koki.sdk.KokiModules
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ModuleService(
    private val koki: KokiModules,
    private val mapper: ModuleMapper,
    private val objectMapper: ObjectMapper,
    private val toggleHolder: TogglesHolder,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ModuleService::class.java)
    }

    private var all: List<ModuleModel>? = null
    private var layoutDescriptor: LayoutDescriptor = LayoutDescriptor()

    @PostConstruct
    fun init() {
        LOGGER.info("Loading module descriptor")

        val path = "/layout/__layout.json"
        val input = this::class.java.getResourceAsStream(path)
        if (input == null) {
            throw IllegalStateException("Layout descriptor not found: $path")
        }

        layoutDescriptor = objectMapper.readValue(input, LayoutDescriptor::class.java)
    }

    fun modules(): List<ModuleModel> {
        if (all == null) {
            val toggles = toggleHolder.get()
            val modules = koki.modules().modules
                .filter { module -> toggles.isModuleEnabled(module.name) }
            LOGGER.info("${modules.size} modules(s) loaded")

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
            }.sortedBy { module ->
                val index = layoutDescriptor.tabs.indexOf(module.name)
                if (index >= 0) {
                    index
                } else {
                    Integer.MAX_VALUE
                }
            }
        }
        return all!!
    }

    fun permissions(): List<PermissionModel> {
        return modules().flatMap { module -> module.permissions }
            .sortedBy { permission -> permission.name }
    }

    fun permissions(permissionIds: List<Long>): List<PermissionModel> {
        return permissions().filter { permission -> permissionIds.contains(permission.id) }
    }
}
