package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.springframework.stereotype.Service
import java.net.URI

@Service
class InstagramURLParser : VideoURLParser {

    companion object {
        private val INSTAGRAM_HOSTS = listOf("instagram.com", "www.instagram.com")
        private val REEL_PATH_REGEX = Regex("/(?:reel|reels|p)/([A-Za-z0-9_-]+)/?")
    }

    override fun getType(): VideoType = VideoType.INSTAGRAM

    override fun supports(url: String): Boolean {
        return try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: return false
            INSTAGRAM_HOSTS.any { host == it || host.endsWith(".$it") }
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(url: String): String? {
        return try {
            val uri = URI(url)
            val path = uri.path ?: return null

            // Format: https://www.instagram.com/reels/VIDEO_ID or https://www.instagram.com/p/VIDEO_ID
            REEL_PATH_REGEX.find(path)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}
