package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractEmployeeController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "employee"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
