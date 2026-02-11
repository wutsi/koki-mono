package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VideoURLParserFactoryTest {

    private val factory = VideoURLParserFactory(
        listOf(
            YouTubeURLParser(),
            TikTokURLParser(),
            InstagramURLParser(),
        )
    )

    @Test
    fun `getParser - returns YouTubeURLParser for YouTube URL`() {
        val parser = factory.getParser("https://www.youtube.com/watch?v=abc123")
        assertEquals(VideoType.YOUTUBE, parser?.getType())
    }

    @Test
    fun `getParser - returns YouTubeURLParser for youtu_be URL`() {
        val parser = factory.getParser("https://youtu.be/abc123")
        assertEquals(VideoType.YOUTUBE, parser?.getType())
    }

    @Test
    fun `getParser - returns TikTokURLParser for TikTok URL`() {
        val parser = factory.getParser("https://www.tiktok.com/@user/video/123")
        assertEquals(VideoType.TIKTOK, parser?.getType())
    }

    @Test
    fun `getParser - returns InstagramURLParser for Instagram reels URL`() {
        val parser = factory.getParser("https://www.instagram.com/reels/abc123")
        assertEquals(VideoType.INSTAGRAM, parser?.getType())
    }

    @Test
    fun `getParser - returns InstagramURLParser for Instagram p URL`() {
        val parser = factory.getParser("https://www.instagram.com/p/abc123")
        assertEquals(VideoType.INSTAGRAM, parser?.getType())
    }

    @Test
    fun `getParser - returns null for unsupported URL`() {
        assertNull(factory.getParser("https://www.vimeo.com/video/123"))
    }

    @Test
    fun `getParser - returns null for invalid URL`() {
        assertNull(factory.getParser("not-a-valid-url"))
    }
}
