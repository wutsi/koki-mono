package com.wutsi.koki.listing.server.service.video

import org.springframework.stereotype.Service

@Service
class VideoURLParserFactory(
    private val parsers: List<VideoURLParser>
) {
    /**
     * Finds a parser that supports the given URL.
     * @param url The video URL
     * @return The appropriate parser, or null if no parser supports the URL
     */
    fun getParser(url: String): VideoURLParser? {
        return parsers.find { it.supports(url) }
    }
}
