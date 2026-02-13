# Implementation Plan: Listing Video Integration

## Overview

This document provides a detailed implementation plan for integrating video information into listings. The feature
allows real estate agents to link videos from social media platforms (TikTok, YouTube, Instagram) to their listings.

---

## 1. Architecture Overview

The implementation spans across multiple layers:

| Layer    | Module        | Component(s)                                                                  |
|----------|---------------|-------------------------------------------------------------------------------|
| Domain   | `koki-server` | `ListingEntity`                                                               |
| DTO      | `koki-dto`    | `VideoType`, `Listing`, `ListingSummary`, `LinkListingVideoRequest`           |
| Service  | `koki-server` | `VideoURLParser`, `YouTubeURLParser`, `TikTokURLParser`, `InstagramURLParser` |
| API      | `koki-server` | `ListingEndpoints`                                                            |
| Mapper   | `koki-server` | `ListingMapper`                                                               |
| SDK      | `koki-sdk`    | `KokiListings`                                                                |
| Database | `koki-server` | Flyway migration                                                              |

---

## 2. Database Layer

### 2.1 Migration Script

Create a new Flyway migration to add the `video_id` and `video_type` columns to the `T_LISTING` table.

**File:** `modules/koki-server/src/main/resources/db/migration/common/V1_62__listing_video.sql`

```sql
-- Add video fields to listing table
ALTER TABLE T_LISTING
    ADD COLUMN video_id   VARCHAR(36),
    ADD COLUMN video_type INT;
```

---

## 3. DTO Layer

### 3.1 Create VideoType Enum

Create an enum to represent the supported video platforms.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/VideoType.kt`

```kotlin
package com.wutsi.koki.listing.dto

enum class VideoType {
    UNKNOWN,
    YOUTUBE,
    TIKTOK,
    INSTAGRAM,
}
```

### 3.2 Create LinkListingVideoRequest DTO

Create a request DTO for the video linking endpoint.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/LinkListingVideoRequest.kt`

```kotlin
package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotBlank

data class LinkListingVideoRequest(
    @get:NotBlank val videoUrl: String = "",
)
```

### 3.3 Update Listing DTO

Add video-related fields to the `Listing` DTO class.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/Listing.kt`

```kotlin
// Add after existing fields (near the end, before createdById)
val videoId: String? = null,
val videoType: VideoType? = null,
val videoEmbedUrl: String? = null,
```

### 3.4 Update ListingSummary DTO

Add video-related fields to the `ListingSummary` DTO class.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ListingSummary.kt`

```kotlin
// Add after existing fields (near publicUrlFr)
val videoId: String? = null,
val videoType: VideoType? = null,
```

### 3.5 Add New Error Code

Add a new error code for unsupported video URLs.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/error/dto/ErrorCode.kt`

```kotlin
// Add under existing LISTING error codes
val LISTING_VIDEO_NOT_SUPPORTED: String = "$PREFIX:listing:video-not-supported"
```

---

## 4. Domain Layer

### 4.1 Update ListingEntity

Add the `videoId` and `videoType` fields to the `ListingEntity` class.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/domain/ListingEntity.kt`

```kotlin
// Add after averageImageQualityScore field
var videoId: String? = null,
var videoType: VideoType? = null,
```

Note: Also add the import for `VideoType`:

```kotlin
import com.wutsi.koki.listing.dto.VideoType
```

---

## 5. Service Layer

### 5.1 Create VideoURLParser Interface

Create an interface for parsing video URLs using the Strategy pattern.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParser.kt`

```kotlin
package com.wutsi.koki.listing.server.service.video

import com.wutsi.koki.listing.dto.VideoType

interface VideoURLParser {
    /**
     * Returns the video type this parser handles.
     */
    fun getType(): VideoType

    /**
     * Checks if this parser supports the given URL.
     * @param url The video URL to check
     * @return true if this parser can handle the URL
     */
    fun supports(url: String): Boolean

    /**
     * Parses the video URL and extracts the video ID.
     * @param url The video URL to parse
     * @return The extracted video ID, or null if parsing fails
     */
    fun parse(url: String): String?
}
```

### 5.2 Create YouTubeURLParser

Implementation for parsing YouTube URLs.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/YouTubeURLParser.kt`

```kotlin
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
```

### 5.3 Create TikTokURLParser

Implementation for parsing TikTok URLs.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/TikTokURLParser.kt`

```kotlin
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
```

### 5.4 Create InstagramURLParser

Implementation for parsing Instagram URLs.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/InstagramURLParser.kt`

```kotlin
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
```

### 5.5 Create VideoURLParserFactory

Factory to retrieve the appropriate parser based on URL.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParserFactory.kt`

```kotlin
package com.wutsi.koki.listing.server.service.video

import org.springframework.stereotype.Service

@Service
class VideoURLParserFactory(
    private val parsers: List<VideoURLParser>
) {
    /**
     * Finds a parser that supports the given URL.
     * @param url The video URL
     * @return The appropriate parser, or null if no parser supports the URL
     */
    fun getParser(url: String): VideoURLParser? {
        return parsers.find { it.supports(url) }
    }
}
```

### 5.6 Create VideoEmbedUrlGenerator

Utility class to generate embed URLs for videos.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoEmbedUrlGenerator.kt`

```kotlin
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
```

### 5.7 Update ListingService

Add the video linking method to the `ListingService`.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingService.kt`

```kotlin
// Add imports at the top
import com.wutsi.koki.listing.dto.LinkListingVideoRequest
import com.wutsi.koki.listing.server.service.video.VideoURLParserFactory

// Add dependency to constructor
class ListingService(
    // ... existing dependencies ...
    private val videoURLParserFactory: VideoURLParserFactory,
) {
    // ... existing methods ...

    @Transactional
    fun video(id: Long, request: LinkListingVideoRequest, tenantId: Long): ListingEntity {
        val listing = get(id, tenantId)

        val parser = videoURLParserFactory.getParser(request.videoUrl)
            ?: throw ConflictException(Error(ErrorCode.LISTING_VIDEO_NOT_SUPPORTED))

        val videoId = parser.parse(request.videoUrl)
            ?: throw ConflictException(Error(ErrorCode.LISTING_VIDEO_NOT_SUPPORTED))

        listing.videoId = videoId
        listing.videoType = parser.getType()
        listing.modifiedAt = Date()
        listing.modifiedById = securityService.getCurrentUserIdOrNull()

        logger.add("video_id", videoId)
        logger.add("video_type", parser.getType())

        return dao.save(listing)
    }
}
```

---

## 6. Mapper Layer

### 6.1 Update ListingMapper

Map the video fields from entity to DTO.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`

```kotlin
// Add import at the top
import com.wutsi.koki.listing.dto.VideoType
import com.wutsi.koki.listing.server.service.video.VideoEmbedUrlGenerator

// Update class to inject VideoEmbedUrlGenerator
@Service
class ListingMapper(
    private val videoEmbedUrlGenerator: VideoEmbedUrlGenerator,
) {
    // Update toListing method to include video fields
    fun toListing(entity: ListingEntity): Listing {
        return Listing(
            // ... existing fields ...
            averageImageQualityScore = entity.averageImageQualityScore,
            videoId = entity.videoId,
            videoType = entity.videoType?.takeIf { it != VideoType.UNKNOWN },
            videoEmbedUrl = videoEmbedUrlGenerator.generate(entity.videoId, entity.videoType),
        )
    }

    // Update toListingSummary method to include video fields
    fun toListingSummary(entity: ListingEntity): ListingSummary {
        return ListingSummary(
            // ... existing fields ...
            publicUrlFr = toPublicUrl(entity.id, entity.titleFr, entity.status),
            videoId = entity.videoId,
            videoType = entity.videoType?.takeIf { it != VideoType.UNKNOWN },
        )
    }
}
```

---

## 7. API Layer

### 7.1 Update ListingEndpoints

Add the video linking endpoint.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/endpoint/ListingEndpoints.kt`

```kotlin
// Add import
import com.wutsi.koki.listing.dto.LinkListingVideoRequest

// Add endpoint method
@PostMapping("/{id}/video")
fun linkVideo(
    @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
    @PathVariable id: Long,
    @Valid @RequestBody request: LinkListingVideoRequest,
) {
    service.video(id, request, tenantId)
}
```

---

## 8. SDK Layer

### 8.1 Update KokiListings

Add the video linking method to the SDK.

**File:** `modules/koki-sdk/src/main/kotlin/com/wutsi/koki/sdk/KokiListings.kt`

```kotlin
// Add import
import com.wutsi.koki.listing.dto.LinkListingVideoRequest

// Add method
fun linkVideo(id: Long, request: LinkListingVideoRequest) {
    val url = urlBuilder.build("$PATH_PREFIX/$id/video")
    rest.postForEntity(url, request, Any::class.java)
}
```

---

## 9. Testing Strategy

### 9.1 Unit Tests for VideoURLParser Implementations

#### 9.1.1 YouTubeURLParserTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/YouTubeURLParserTest.kt`

```kotlin
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
```

#### 9.1.2 TikTokURLParserTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/TikTokURLParserTest.kt`

```kotlin
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
```

#### 9.1.3 InstagramURLParserTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/InstagramURLParserTest.kt`

```kotlin
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
```

#### 9.1.4 VideoURLParserFactoryTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParserFactoryTest.kt`

```kotlin
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
```

#### 9.1.5 VideoEmbedUrlGeneratorTest

**File:**`modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/VideoEmbedUrlGeneratorTest.kt`

```kotlin
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
```

### 9.2 Integration Tests for API Endpoint

#### 9.2.1 LinkListingVideoEndpointTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/endpoint/LinkListingVideoEndpointTest.kt`

```kotlin
package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.LinkListingVideoRequest
import com.wutsi.koki.listing.dto.VideoType
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingVideoLinkEndpoint.sql"])
class LinkListingVideoEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun `link YouTube video`() {
        val id = 100L
        val request = LinkListingVideoRequest(
            videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
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
        val request = LinkListingVideoRequest(
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
        val request = LinkListingVideoRequest(
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
        val request = LinkListingVideoRequest(
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
        val request = LinkListingVideoRequest(
            videoUrl = "https://www.vimeo.com/video/123456"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `invalid video URL returns 409`() {
        val id = 100L
        val request = LinkListingVideoRequest(
            videoUrl = "not-a-valid-url"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `listing not found returns 404`() {
        val id = 99999L
        val request = LinkListingVideoRequest(
            videoUrl = "https://www.youtube.com/watch?v=abc123"
        )
        val response = rest.postForEntity("/v1/listings/$id/video", request, Any::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
```

#### 9.2.2 Test Data SQL

**File:** `modules/koki-server/src/test/resources/db/test/listing/UpdateListingVideoLinkEndpoint.sql`

```sql
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, number_format, monetary_format, date_format, time_format,
                     date_time_format, logo_url, icon_url)
VALUES (1, 'test', 'localhost', 'en_US', 'XAF', '#,###', '#,### XAF', 'yyyy-MM-dd', 'HH:mm', 'yyyy-MM-dd HH:mm',
        'https://logo.png', 'https://icon.png');

INSERT INTO T_USER(id, tenant_fk, email, display_name, status, type)
VALUES (11, 1, 'ray.sponsible@gmail.com', 'Ray Sponsible', 1, 1);

-- Listing without video
INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, bedrooms, bathrooms, price, currency,
                      created_by_fk)
VALUES (100, 1, 1, 'SALE', 'HOUSE', 3, 2, 50000000, 'XAF', 11);

-- Listing with existing video (to test overwrite)
INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, bedrooms, bathrooms, price, currency,
                      created_by_fk, video_id, video_type)
VALUES (101, 1, 1, 'SALE', 'HOUSE', 3, 2, 50000000, 'XAF', 11, 'oldVideoId', 2);
```

### 9.3 Update GetListingEndpointTest

Update the existing test to verify video fields are returned in the response.

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/endpoint/GetListingEndpointTest.kt`

Add test case:

```kotlin
@Test
fun `get listing with video returns video fields`() {
    // Test that videoId, videoType, and videoEmbedUrl are correctly returned
    // Assumes a test listing with video data exists
}
```

---

## 10. Test Cases Summary

| Category        | Test Case                                             | Expected Result                 |
|-----------------|-------------------------------------------------------|---------------------------------|
| YouTubeParser   | Parse `youtube.com/watch?v=ID`                        | Returns video ID                |
| YouTubeParser   | Parse `youtu.be/ID`                                   | Returns video ID                |
| YouTubeParser   | Parse URL with query params                           | Strips params, returns video ID |
| YouTubeParser   | Parse URL with trailing slash                         | Strips slash, returns video ID  |
| YouTubeParser   | Parse invalid URL                                     | Returns null                    |
| TikTokParser    | Parse `tiktok.com/@user/video/ID`                     | Returns video ID                |
| TikTokParser    | Parse URL with trailing slash                         | Strips slash, returns video ID  |
| TikTokParser    | Parse non-video URL                                   | Returns null                    |
| InstagramParser | Parse `instagram.com/reels/ID`                        | Returns video ID                |
| InstagramParser | Parse `instagram.com/reel/ID`                         | Returns video ID                |
| InstagramParser | Parse `instagram.com/p/ID`                            | Returns video ID                |
| InstagramParser | Parse profile URL                                     | Returns null                    |
| Factory         | Get parser for YouTube URL                            | Returns YouTubeURLParser        |
| Factory         | Get parser for TikTok URL                             | Returns TikTokURLParser         |
| Factory         | Get parser for Instagram URL                          | Returns InstagramURLParser      |
| Factory         | Get parser for unsupported URL                        | Returns null                    |
| EmbedGenerator  | Generate YouTube embed URL                            | Returns embed URL               |
| EmbedGenerator  | Generate TikTok embed URL                             | Returns player URL              |
| EmbedGenerator  | Generate Instagram embed URL                          | Returns embed URL               |
| EmbedGenerator  | Generate with null videoId                            | Returns null                    |
| EmbedGenerator  | Generate with null videoType                          | Returns null                    |
| Endpoint        | Link YouTube video                                    | Saves videoId and videoType     |
| Endpoint        | Link TikTok video                                     | Saves videoId and videoType     |
| Endpoint        | Link Instagram video                                  | Saves videoId and videoType     |
| Endpoint        | Overwrite existing video                              | Replaces previous video         |
| Endpoint        | Unsupported video URL                                 | Returns 409 CONFLICT            |
| Endpoint        | Invalid URL format                                    | Returns 409 CONFLICT            |
| Endpoint        | Listing not found                                     | Returns 404 NOT FOUND           |
| DTO             | Get listing returns videoId, videoType, videoEmbedUrl | Fields populated correctly      |
| DTO             | Get listing summary returns videoId, videoType        | Fields populated correctly      |

---

## 11. Implementation Checklist

### Phase 1: Database & DTO Layer

- [x] Create Flyway migration `V1_62__listing_video.sql`
- [x] Create `VideoType` enum in koki-dto
- [x] Create `LinkListingVideoRequest` DTO in koki-dto
- [x] Add `videoId`, `videoType`, `videoEmbedUrl` fields to `Listing` DTO
- [x] Add `videoId`, `videoType` fields to `ListingSummary` DTO
- [x] Add `LISTING_VIDEO_NOT_SUPPORTED` error code to `ErrorCode`

### Phase 2: Domain Layer

- [x] Add `videoId` and `videoType` fields to `ListingEntity`

### Phase 3: Service Layer

- [x] Create `VideoURLParser` interface
- [x] Create `YouTubeURLParser` implementation
- [x] Create `TikTokURLParser` implementation
- [x] Create `InstagramURLParser` implementation
- [x] Create `VideoURLParserFactory`
- [x] Create `VideoEmbedUrlGenerator`
- [x] Add unit tests for all parser implementations
- [x] Add unit tests for `VideoURLParserFactory`
- [x] Add unit tests for `VideoEmbedUrlGenerator`

### Phase 4: Mapper Layer

- [x] Update `ListingMapper` to inject `VideoEmbedUrlGenerator`
- [x] Update `toListing()` to map video fields
- [x] Update `toListingSummary()` to map video fields

### Phase 5: API Layer

- [x] Add `linkVideo()` endpoint to `ListingEndpoints`
- [x] Add `video()` method to `ListingService`
- [x] Create integration test `LinkListingVideoEndpointTest`
- [x] Create test SQL data file

### Phase 6: SDK Layer

- [x] Add `linkVideo()` method to `KokiListings`

### Phase 7: Final Validation

- [ ] Run all unit tests
- [ ] Run all integration tests
- [ ] Test manually with real video URLs from YouTube, TikTok, and Instagram
- [ ] Verify video embed URLs work correctly in browser

---

## 12. File Creation Summary

### New Files to Create

| File Path                                                                                                       | Description                |
|-----------------------------------------------------------------------------------------------------------------|----------------------------|
| `modules/koki-server/src/main/resources/db/migration/common/V1_62__listing_video.sql`                           | Database migration         |
| `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/VideoType.kt`                                      | VideoType enum             |
| `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/LinkListingVideoRequest.kt`                        | Request DTO                |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParser.kt`             | Parser interface           |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/YouTubeURLParser.kt`           | YouTube parser             |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/TikTokURLParser.kt`            | TikTok parser              |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/InstagramURLParser.kt`         | Instagram parser           |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParserFactory.kt`      | Parser factory             |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/video/VideoEmbedUrlGenerator.kt`     | Embed URL generator        |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/YouTubeURLParserTest.kt`       | YouTube parser tests       |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/TikTokURLParserTest.kt`        | TikTok parser tests        |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/InstagramURLParserTest.kt`     | Instagram parser tests     |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/VideoURLParserFactoryTest.kt`  | Factory tests              |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/video/VideoEmbedUrlGeneratorTest.kt` | Embed generator tests      |
| `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/endpoint/LinkListingVideoEndpointTest.kt`    | Endpoint integration tests |
| `modules/koki-server/src/test/resources/db/test/listing/UpdateListingVideoLinkEndpoint.sql`                     | Test data SQL              |

### Files to Modify

| File Path                                                                                        | Changes                                          |
|--------------------------------------------------------------------------------------------------|--------------------------------------------------|
| `modules/koki-dto/src/main/kotlin/com/wutsi/koki/error/dto/ErrorCode.kt`                         | Add LISTING_VIDEO_NOT_SUPPORTED                  |
| `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/Listing.kt`                         | Add videoId, videoType, videoEmbedUrl            |
| `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ListingSummary.kt`                  | Add videoId, videoType                           |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/domain/ListingEntity.kt`      | Add videoId, videoType                           |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`      | Map video fields, inject VideoEmbedUrlGenerator  |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingService.kt`    | Add video() method, inject VideoURLParserFactory |
| `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/endpoint/ListingEndpoints.kt` | Add linkVideo endpoint                           |
| `modules/koki-sdk/src/main/kotlin/com/wutsi/koki/sdk/KokiListings.kt`                            | Add linkVideo method                             |
