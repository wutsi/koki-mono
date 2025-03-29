package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractFormController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "form"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
