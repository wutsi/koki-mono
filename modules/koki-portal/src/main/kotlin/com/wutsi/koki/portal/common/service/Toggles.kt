package com.wutsi.koki.portal.common.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "koki.toggles")
class Toggles {
    var paypal: Boolean = false
    var mobileMoney: Boolean = false
    var coBrokering: Boolean = false
    var modules: ModuleToggles = ModuleToggles()

    fun isModuleEnabled(name: String): Boolean {
        return when (name) {
            "agent" -> modules.agent
            "account" -> modules.account
            "contact" -> modules.contact
            "file" -> modules.file
            "image" -> modules.image
            "lead" -> modules.lead
            "listing" -> modules.listing
            "offer" -> modules.offer
            else -> true
        }
    }
}

class ModuleToggles {
    var agent: Boolean = false
    var account: Boolean = false
    var contact: Boolean = false
    var file: Boolean = false
    var image: Boolean = false
    var lead: Boolean = false
    var listing: Boolean = false
    var offer: Boolean = false
}
