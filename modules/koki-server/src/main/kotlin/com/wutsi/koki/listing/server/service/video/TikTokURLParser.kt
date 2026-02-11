package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.springframework.stereotype.Service
import java.net.URI

@Service
class TikTokURLParser : VideoURLParser {

    companion object {
        private val TIKTOK_HOSTS = listOf("tiktok.com", "www.tiktok.com")
        private val VIDEO_PATH_REGEX = Regex(".*/video/(\\d+).*")
    }

    override fun getType(): VideoType = VideoType.TIKTOK

    override fun supports(url: String): Boolean {
        return try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: return false
            TIKTOK_HOSTS.any { host == it || host.endsWith(".$it") }
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(url: String): String? {
        return try {
            val uri = URI(url)
            val path = uri.path?.trimEnd('/') ?: return null

            // Format: https://www.tiktok.com/@USER/video/VIDEO_ID
            VIDEO_PATH_REGEX.matchEntire(path)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}
