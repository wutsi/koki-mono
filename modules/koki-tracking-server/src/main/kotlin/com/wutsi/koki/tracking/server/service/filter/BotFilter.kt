package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.springframework.stereotype.Service
import ua_parser.Parser

@Service
class BotFilter(private val logger: KVLogger) : Filter {
    private val uaParser = Parser()

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ua == null) {
            return track
        }
        val client = uaParser.parse(track.ua)
        val bot = client.device.family.equals("spider", true)
        logger.add("track_bot", bot)
        return track.copy(bot = bot)
    }
}
