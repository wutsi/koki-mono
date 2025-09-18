package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.offer.service.OfferService
import com.wutsi.koki.portal.offer.service.OfferVersionService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractOfferDetailsController : AbstractModuleDetailsPageController() {
    companion object {
        const val MODULE_NAME = "offer"
    }

    @Autowired
    protected lateinit var offerService: OfferService

    @Autowired
    protected lateinit var offerVersionService: OfferVersionService

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected open fun findOffer(id: Long): OfferModel {
        return offerService.get(id)
    }
}
