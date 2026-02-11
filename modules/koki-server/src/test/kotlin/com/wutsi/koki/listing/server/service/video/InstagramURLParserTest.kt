package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InstagramURLParserTest {

    private val parser = InstagramURLParser()

    @Test
    fun `getType returns INSTAGRAM`() {
        assertEquals(VideoType.INSTAGRAM, parser.getType())
    }

    // supports() tests

    @Test
    fun `supports - instagram reels URL`() {
        assertTrue(parser.supports("https://www.instagram.com/reels/CxYzAbC123"))
    }

    @Test
    fun `supports - instagram reel URL`() {
        assertTrue(parser.supports("https://www.instagram.com/reel/CxYzAbC123"))
    }

    @Test
    fun `supports - instagram p URL`() {
        assertTrue(parser.supports("https://www.instagram.com/p/CxYzAbC123"))
    }

    @Test
    fun `supports - instagram without www`() {
        assertTrue(parser.supports("https://instagram.com/reels/CxYzAbC123"))
    }

    @Test
    fun `supports - returns false for YouTube`() {
        assertFalse(parser.supports("https://www.youtube.com/watch?v=abc123"))
    }

    @Test
    fun `supports - returns false for TikTok`() {
        assertFalse(parser.supports("https://www.tiktok.com/@user/video/123"))
    }

    @Test
    fun `supports - returns false for invalid URL`() {
        assertFalse(parser.supports("not-a-url"))
    }

    // parse() tests

    @Test
    fun `parse - instagram reels URL`() {
        assertEquals("CxYzAbC123", parser.parse("https://www.instagram.com/reels/CxYzAbC123"))
    }

    @Test
    fun `parse - instagram reel URL`() {
        assertEquals("CxYzAbC123", parser.parse("https://www.instagram.com/reel/CxYzAbC123"))
    }

    @Test
    fun `parse - instagram p URL`() {
        assertEquals("CxYzAbC123", parser.parse("https://www.instagram.com/p/CxYzAbC123"))
    }

    @Test
    fun `parse - instagram reels with trailing slash`() {
        assertEquals("CxYzAbC123", parser.parse("https://www.instagram.com/reels/CxYzAbC123/"))
    }

    @Test
    fun `parse - instagram reels with query params`() {
        assertEquals("CxYzAbC123", parser.parse("https://www.instagram.com/reels/CxYzAbC123?utm_source=share"))
    }

    @Test
    fun `parse - returns null for profile URL`() {
        assertNull(parser.parse("https://www.instagram.com/username"))
    }

    @Test
    fun `parse - returns null for invalid URL`() {
        assertNull(parser.parse("not-a-valid-url"))
    }
}
