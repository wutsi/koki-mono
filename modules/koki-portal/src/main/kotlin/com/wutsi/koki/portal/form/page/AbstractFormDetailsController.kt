package com.wutsi.koki.portal.form.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractFormDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractFormController.MODULE_NAME
    }
}
