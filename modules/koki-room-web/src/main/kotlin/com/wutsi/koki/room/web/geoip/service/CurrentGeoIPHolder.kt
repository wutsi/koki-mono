package com.wutsi.koki.room.web.geoip.service

import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.room.web.geoip.mapper.GeoIpMapper
import com.wutsi.koki.room.web.geoip.model.GeoIpModel
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentGeoIPHolder(
    private val service: GeoIpService,
    private val mapper: GeoIpMapper,
    private val logger: KVLogger,
    private val request: HttpServletRequest,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CurrentGeoIPHolder::class.java)
    }

    var data: GeoIpModel? = null

    fun get(): GeoIpModel? {
        if (data == null) {
            try {
                val ip = getIp()
                logger.add("ip", ip)

                data = service.resolve(ip)?.let { geoip -> mapper.toGeoIpMapper(geoip) }
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

    private fun getIp(): String {
        return request.getHeader("X-FORWARDED-FOR")?.ifEmpty { null } ?: request.remoteAddr
    }
}
