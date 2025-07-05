package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.springframework.stereotype.Service
import ua_parser.Parser

@Service
class DeviceTypeFilter : Filter {
    private val uaParser = Parser()

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ua == null) {
            return track
        }
        return track.copy(deviceType = detect(track.ua))
    }

    private fun detect(ua: String): DeviceType {
        if (ua.contains("(dart:io)", true)) {
            return DeviceType.MOBILE
        } else if ((ua.contains("Android", true) && !ua.contains("Mobile", true)) || ua.contains("iPad", true)) {
            return DeviceType.TABLET
        }

        val client = uaParser.parse(ua)
        return if (client.device.family.equals("spider", true)) { // Bot
            DeviceType.UNKNOWN
        } else if (client.userAgent.family.contains("mobile", true)) {
            DeviceType.MOBILE
        } else if (client.userAgent.family.contains("other", true)) {
            DeviceType.UNKNOWN
        } else {
            DeviceType.DESKTOP
        }
    }
}
