package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.springframework.stereotype.Service

@Service
class VideoEmbedUrlGenerator {

    /**
     * Generates the embed URL for a video.
     * @param videoId The video ID
     * @param videoType The type of video platform
     * @return The embed URL, or null if videoId or videoType is null
     */
    fun generate(videoId: String?, videoType: VideoType?): String? {
        if (videoId == null || videoType == null || videoType == VideoType.UNKNOWN) {
            return null
        }

        return when (videoType) {
            VideoType.YOUTUBE -> "https://www.youtube.com/embed/$videoId"
            VideoType.TIKTOK -> "https://www.tiktok.com/player/v1/$videoId"
            VideoType.INSTAGRAM -> "https://www.instagram.com/p/$videoId/embed/"
            else -> null
        }
    }
}
