package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController

abstract class AbstractListingDetailsController : AbstractModuleDetailsPageController() {
    override fun getModuleName(): String {
        return AbstractListingController.MODULE_NAME
    }
}
