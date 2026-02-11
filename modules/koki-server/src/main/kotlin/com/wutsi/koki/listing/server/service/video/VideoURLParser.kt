package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType

interface VideoURLParser {
    /**
     * Returns the video type this parser handles.
     */
    fun getType(): VideoType

    /**
     * Checks if this parser supports the given URL.
     * @param url The video URL to check
     * @return true if this parser can handle the URL
     */
    fun supports(url: String): Boolean

    /**
     * Parses the video URL and extracts the video ID.
     * @param url The video URL to parse
     * @return The extracted video ID, or null if parsing fails
     */
    fun parse(url: String): String?
}
