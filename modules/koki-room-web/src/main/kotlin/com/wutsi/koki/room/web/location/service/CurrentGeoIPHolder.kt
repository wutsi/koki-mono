package com.wutsi.koki.room.web.location.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.room.web.location.model.GeoIpModel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentGeoIPHolder(
    private val rest: RestTemplate,
    private val logger: KVLogger
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CurrentGeoIPHolder::class.java)
    }

    var data: GeoIpModel? = null

    fun get(): GeoIpModel? {
        if (data == null) {
            try {
                data = fetch()

                logger.add("geoip_country", data?.countryCode)
                logger.add("geoip_city", data?.city)
                logger.add("geoip_longitude", data?.longitude)
                logger.add("geoip_latitude", data?.latitude)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to fetch geo-ip information", ex)
            }
        }
        return data
    }

    private fun fetch(): GeoIpModel {
        return rest.getForEntity("https://ipapi.co/json", GeoIpModel::class.java).body
    }
}
