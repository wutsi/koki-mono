package com.wutsi.koki.portal.offer.page.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController

abstract class AbstractOfferController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "offer"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
