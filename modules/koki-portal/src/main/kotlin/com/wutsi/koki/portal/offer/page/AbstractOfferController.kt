package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.module.page.AbstractModulePageController
import com.wutsi.koki.portal.offer.service.OfferService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractOfferController : AbstractModulePageController() {
    companion object {
        const val MODULE_NAME = "offer"
    }

    @Autowired
    protected lateinit var offerService: OfferService

    override fun getModuleName(): String {
        return MODULE_NAME
    }
}
