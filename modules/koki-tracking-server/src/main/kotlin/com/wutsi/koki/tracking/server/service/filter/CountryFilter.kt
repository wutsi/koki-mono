package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import com.wutsi.koki.tracking.server.service.IpApiService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CountryFilter(private val service: IpApiService) : Filter {
    companion object{
        private val LOGGER = LoggerFactory.getLogger(CountryFilter::class.java)
    }

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ip != null) {
            try {
                return track.copy(country = service.resolveCountry(track.ip))
            } catch(ex: Exception){
                LOGGER.warn("Unable to resolve location information from ${track.ip}", ex)
                return track
            }
        } else {
            return track
        }
    }
}
