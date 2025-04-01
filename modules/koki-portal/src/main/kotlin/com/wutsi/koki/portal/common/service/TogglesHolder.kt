package com.wutsi.koki.portal.common.service

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesHolder(
    private val toggles: Toggles
) {
    fun get(): Toggles = toggles
}
