package com.wutsi.koki.portal.module.page

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.module.model.PageLayoutDescriptor
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory

abstract class AbstractModuleDetailsPageController : AbstractModulePageController() {
    private var layoutDescriptor = PageLayoutDescriptor()

    @PostConstruct
    fun init() {
        val logger = LoggerFactory.getLogger(this::class.java)
        logger.info("Loading layout descriptor")

        val moduleName = getModuleName()
        val path = "/layout/$moduleName.json"
        val input = this::class.java.getResourceAsStream(path)
        if (input == null) {
            throw IllegalStateException("Layout descriptor not found: $path")
        }

        layoutDescriptor = jsonMapper.readValue(input, PageLayoutDescriptor::class.java)
    }

    override fun createPageModel(name: String, title: String): PageModel {
        val logger = LoggerFactory.getLogger(this::class.java)

        val user = userHolder.get() ?: return super.createPageModel(name, title)
        val tenant = tenantHolder.get() ?: return super.createPageModel(name, title)
        val moduleMap = tenant.modules.associateBy { module -> module.name }

        return PageModel(
            name = name,
            title = title,
            tabs = layoutDescriptor.tabs.map { entry ->
                entry.key to entry.value.mapNotNull { moduleName ->
                    if (logger.isDebugEnabled) {
                        logger.debug("Module: ${entry.key}.$moduleName")
                    }
                    moduleMap[moduleName]?.let { module ->
                        if (!user.canAccess(module)) null else module
                    }
                }
            }.toMap(),
            assetUrl = assetUrl,
        )
    }
}
