package com.wutsi.koki.portal.module.page

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.page.AbstractPageController
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractModulePageController : AbstractPageController() {
    abstract fun getModuleName(): String

    protected open fun createPageModel(name: String, title: String): PageModel {
        return PageModel(
            name = name,
            title = title,
        )
    }

    @ModelAttribute("module")
    fun getModule(): ModuleModel {
        val name = getModuleName()
        return tenantHolder.get()!!.modules.find { module -> module.name == name }
            ?: throw IllegalStateException("Invalid module: $name")
    }
}
