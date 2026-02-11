package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VideoEmbedUrlGeneratorTest {

    private val generator = VideoEmbedUrlGenerator()

    @Test
    fun `generate - YouTube embed URL`() {
        assertEquals(
            "https://www.youtube.com/embed/dQw4w9WgXcQ",
            generator.generate("dQw4w9WgXcQ", VideoType.YOUTUBE)
        )
    }

    @Test
    fun `generate - TikTok embed URL`() {
        assertEquals(
            "https://www.tiktok.com/player/v1/7123456789012345678",
            generator.generate("7123456789012345678", VideoType.TIKTOK)
        )
    }

    @Test
    fun `generate - Instagram embed URL`() {
        assertEquals(
            "https://www.instagram.com/p/CxYzAbC123/embed/",
            generator.generate("CxYzAbC123", VideoType.INSTAGRAM)
        )
    }

    @Test
    fun `generate - returns null when videoId is null`() {
        assertNull(generator.generate(null, VideoType.YOUTUBE))
    }

    @Test
    fun `generate - returns null when videoType is null`() {
        assertNull(generator.generate("abc123", null))
    }

    @Test
    fun `generate - returns null when videoType is UNKNOWN`() {
        assertNull(generator.generate("abc123", VideoType.UNKNOWN))
    }
}
