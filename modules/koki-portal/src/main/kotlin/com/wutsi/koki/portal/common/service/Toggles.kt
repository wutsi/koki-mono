package com.wutsi.koki.portal.common.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "koki.toggles")
class Toggles {
    var paypal: Boolean = false
    var mobileMoney: Boolean = false
}
