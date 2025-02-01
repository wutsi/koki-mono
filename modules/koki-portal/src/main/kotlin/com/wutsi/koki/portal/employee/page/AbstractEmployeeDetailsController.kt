package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractEmployeeDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractEmployeeController.MODULE_NAME
    }
}
