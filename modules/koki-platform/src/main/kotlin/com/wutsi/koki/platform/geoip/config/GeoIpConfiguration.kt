package com.wutsi.koki.platform.geoip.config

import com.wutsi.koki.platform.geoip.GeoIpService
import org.springframework.cache.Cache
import org.springframework.context.annotation.Bean

class GeoIpConfiguration(private val cache: Cache) {
    @Bean
    fun geoIpService(): GeoIpService {
        return GeoIpService(cache)
    }
}
