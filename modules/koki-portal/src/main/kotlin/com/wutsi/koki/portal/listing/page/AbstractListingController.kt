package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractListingController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "listing"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
