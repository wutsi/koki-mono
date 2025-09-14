package com.wutsi.koki.portal.common.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "koki.toggles")
class Toggles {
    var paypal: Boolean = false
    var mobileMoney: Boolean = false
    var modules: ModuleToggles = ModuleToggles()

    fun isModuleEnabled(name: String): Boolean {
        return when (name) {
            "account" -> modules.account
            "contact" -> modules.contact
            "file" -> modules.file
            "image" -> modules.image
            "invoice" -> modules.invoice
            "listing" -> modules.listing
            "message" -> modules.message
            "offer" -> modules.offer
            "product" -> modules.product
            "payment" -> modules.payment
            else -> true
        }
    }
}

class ModuleToggles {
    var account: Boolean = false
    var contact: Boolean = false
    var file: Boolean = false
    var image: Boolean = false
    var invoice: Boolean = false
    var listing: Boolean = false
    var message: Boolean = false
    var offer: Boolean = false
    var product: Boolean = false
    var payment: Boolean = false
}
