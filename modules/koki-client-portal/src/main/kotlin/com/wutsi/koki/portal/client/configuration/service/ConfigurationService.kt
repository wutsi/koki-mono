package com.wutsi.koki.portal.client.configuration.service

import com.wutsi.koki.sdk.KokiConfiguration
import org.springframework.stereotype.Service

@Service
class ConfigurationService(
    private val koki: KokiConfiguration,
) {
    fun configurations(
        names: List<String> = emptyList(),
        keyword: String? = null,
    ): Map<String, String> {
        return koki.configurations(
            names = names,
            keyword = keyword,
        ).configurations.map { config -> config.name to config.value }.toMap()
    }
}
