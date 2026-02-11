package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.springframework.stereotype.Service
import java.net.URI

@Service
class YouTubeURLParser : VideoURLParser {

    companion object {
        private val YOUTUBE_HOSTS = listOf("youtube.com", "www.youtube.com", "youtu.be", "www.youtu.be")
    }

    override fun getType(): VideoType = VideoType.YOUTUBE

    override fun supports(url: String): Boolean {
        return try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: return false
            YOUTUBE_HOSTS.any { host == it || host.endsWith(".$it") }
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(url: String): String? {
        return try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: return null

            when {
                // Format: https://youtu.be/VIDEO_ID
                host == "youtu.be" || host == "www.youtu.be" -> {
                    uri.path?.trimStart('/')?.trimEnd('/')?.takeIf { it.isNotEmpty() }
                }
                // Format: https://www.youtube.com/watch?v=VIDEO_ID
                host.contains("youtube.com") -> {
                    parseQueryParam(uri.query, "v")
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseQueryParam(query: String?, paramName: String): String? {
        return query?.split("&")
            ?.map { it.split("=") }
            ?.find { it.size == 2 && it[0] == paramName }
            ?.get(1)
            ?.trimEnd('/')
    }
}
