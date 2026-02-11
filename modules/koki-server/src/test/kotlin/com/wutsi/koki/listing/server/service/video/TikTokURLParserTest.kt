package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TikTokURLParserTest {

    private val parser = TikTokURLParser()

    @Test
    fun `getType returns TIKTOK`() {
        assertEquals(VideoType.TIKTOK, parser.getType())
    }

    // supports() tests

    @Test
    fun `supports - tiktok video URL`() {
        assertTrue(parser.supports("https://www.tiktok.com/@user/video/1234567890"))
    }

    @Test
    fun `supports - tiktok without www`() {
        assertTrue(parser.supports("https://tiktok.com/@user/video/1234567890"))
    }

    @Test
    fun `supports - returns false for YouTube`() {
        assertFalse(parser.supports("https://www.youtube.com/watch?v=abc123"))
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
    fun `parse - tiktok video URL`() {
        assertEquals("7123456789012345678", parser.parse("https://www.tiktok.com/@username/video/7123456789012345678"))
    }

    @Test
    fun `parse - tiktok video URL with trailing slash`() {
        assertEquals("7123456789012345678", parser.parse("https://www.tiktok.com/@username/video/7123456789012345678/"))
    }

    @Test
    fun `parse - tiktok video URL with query params`() {
        assertEquals(
            "7123456789012345678",
            parser.parse("https://www.tiktok.com/@username/video/7123456789012345678?is_from_webapp=1")
        )
    }

    @Test
    fun `parse - returns null for non-video URL`() {
        assertNull(parser.parse("https://www.tiktok.com/@username"))
    }

    @Test
    fun `parse - returns null for invalid URL`() {
        assertNull(parser.parse("not-a-valid-url"))
    }
}
