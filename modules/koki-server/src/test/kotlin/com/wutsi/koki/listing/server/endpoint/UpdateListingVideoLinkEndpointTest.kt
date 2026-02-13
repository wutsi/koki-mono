package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingVideoLinkRequest
import com.wutsi.koki.listing.dto.VideoType
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingVideoLinkEndpoint.sql"])
class UpdateListingVideoLinkEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun `link YouTube video`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals("dQw4w9WgXcQ", listing.videoId)
        assertEquals(VideoType.YOUTUBE, listing.videoType)
    }

    @Test
    fun `link YouTube short URL`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://youtu.be/dQw4w9WgXcQ"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals("dQw4w9WgXcQ", listing.videoId)
        assertEquals(VideoType.YOUTUBE, listing.videoType)
    }

    @Test
    fun `link TikTok video`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://www.tiktok.com/@user/video/7123456789012345678"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals("7123456789012345678", listing.videoId)
        assertEquals(VideoType.TIKTOK, listing.videoType)
    }

    @Test
    fun `link Instagram video`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://www.instagram.com/reels/CxYzAbC123"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals("CxYzAbC123", listing.videoId)
        assertEquals(VideoType.INSTAGRAM, listing.videoType)
    }

    @Test
    fun `overwrite existing video`() {
        val id = 101L // Listing with existing video
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://youtu.be/newVideoId"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals("newVideoId", listing.videoId)
        assertEquals(VideoType.YOUTUBE, listing.videoType)
    }

    @Test
    fun `unsupported video URL returns 409`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://www.vimeo.com/video/123456"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `invalid video URL returns 409`() {
        val id = 100L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "not-a-valid-url"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `listing not found returns 404`() {
        val id = 99999L
        val request = UpdateListingVideoLinkRequest(
            videoUrl = "https://www.youtube.com/watch?v=abc123"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `null videoUrl clears video`() {
        val id = 101L // Listing with existing video
        val request = UpdateListingVideoLinkRequest(
            videoUrl = null
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(null, listing.videoId)
        assertEquals(null, listing.videoType)
    }
}
