package com.wutsi.koki.portal.module.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.module.model.ModuleModel
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractModulePageController : AbstractPageController() {
    abstract fun getModuleName(): String

    @ModelAttribute("module")
    fun getModule(): ModuleModel {
        val name = getModuleName()
        return tenantHolder.get().modules.find { module -> module.name == name }
            ?: throw IllegalStateException("Invalid module: $name")
    }
}
