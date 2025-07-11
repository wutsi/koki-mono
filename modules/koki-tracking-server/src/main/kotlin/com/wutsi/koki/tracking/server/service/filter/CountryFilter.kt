package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CountryFilter(
    private val service: GeoIpService,
    private val logger: KVLogger,
) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CountryFilter::class.java)
    }

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ip.isNullOrEmpty()) {
            return track
        }

        try {
            val country = service.resolve(track.ip)?.countryCode
            logger.add("track_country", country)
            return track.copy(country = country)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve location information from ${track.ip}", ex)
            return track
        }
    }
}
