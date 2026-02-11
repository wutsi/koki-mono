package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class YouTubeURLParserTest {

    private val parser = YouTubeURLParser()

    @Test
    fun `getType returns YOUTUBE`() {
        assertEquals(VideoType.YOUTUBE, parser.getType())
    }

    // supports() tests

    @Test
    fun `supports - youtube watch URL`() {
        assertTrue(parser.supports("https://www.youtube.com/watch?v=abc123"))
    }

    @Test
    fun `supports - youtu_be short URL`() {
        assertTrue(parser.supports("https://youtu.be/abc123"))
    }

    @Test
    fun `supports - youtube without www`() {
        assertTrue(parser.supports("https://youtube.com/watch?v=abc123"))
    }

    @Test
    fun `supports - returns false for TikTok`() {
        assertFalse(parser.supports("https://www.tiktok.com/@user/video/123"))
    }

    @Test
    fun `supports - returns false for Instagram`() {
        assertFalse(parser.supports("https://www.instagram.com/reels/abc123"))
    }

    @Test
    fun `supports - returns false for invalid URL`() {
        assertFalse(parser.supports("not-a-url"))
    }

    // parse() tests

    @Test
    fun `parse - youtube watch URL`() {
        assertEquals("dQw4w9WgXcQ", parser.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
    }

    @Test
    fun `parse - youtube watch URL with additional params`() {
        assertEquals("dQw4w9WgXcQ", parser.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=120"))
    }

    @Test
    fun `parse - youtu_be short URL`() {
        assertEquals("dQw4w9WgXcQ", parser.parse("https://youtu.be/dQw4w9WgXcQ"))
    }

    @Test
    fun `parse - youtu_be with query params`() {
        assertEquals("dQw4w9WgXcQ", parser.parse("https://youtu.be/dQw4w9WgXcQ?utm_source=share"))
    }

    @Test
    fun `parse - youtu_be with trailing slash`() {
        assertEquals("dQw4w9WgXcQ", parser.parse("https://youtu.be/dQw4w9WgXcQ/"))
    }

    @Test
    fun `parse - returns null for missing video ID`() {
        assertNull(parser.parse("https://www.youtube.com/watch?list=playlist"))
    }

    @Test
    fun `parse - returns null for invalid URL`() {
        assertNull(parser.parse("not-a-valid-url"))
    }
}
