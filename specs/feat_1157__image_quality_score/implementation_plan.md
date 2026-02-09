# Implementation Plan: Average Image Quality Score (AIQS)

## Overview

This document provides a detailed implementation plan for the Average Image Quality Score (AIQS) feature, which computes
the average quality score of all approved images associated with a listing.

---

## 1. Architecture Overview

The implementation will span across multiple layers:

| Layer       | Module        | Component(s)                           |
|-------------|---------------|----------------------------------------|
| Domain      | `koki-server` | `ListingEntity`                        |
| Service     | `koki-server` | `AverageImageQualityScoreService`      |
| Publication | `koki-server` | `ListingPublisher`                     |
| Mapper      | `koki-server` | `ListingMapper`                        |
| DTO         | `koki-dto`    | `Listing`, `ListingSummary` (optional) |
| Database    | `koki-server` | Flyway migration                       |

---

## 2. Database Layer

### 2.1 Migration Script

Create a new Flyway migration to add the `average_image_quality_score` column to the `T_LISTING` table.

**File:** `modules/koki-server/src/main/resources/db/migration/common/V1_61__listing_average_image_quality_score.sql`

```sql
-- Add average image quality score column to listing table
ALTER TABLE T_LISTING
    ADD COLUMN average_image_quality_score DOUBLE PRECISION;
```

---

## 3. Domain Layer

### 3.1 Update ListingEntity

Add the `averageImageQualityScore` field to the `ListingEntity` class.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/domain/ListingEntity.kt`

```kotlin
// Add after qrCodeUrl field
var averageImageQualityScore: Double? = null,
```

---

## 4. DTO Layer

### 4.1 Update Listing DTO

Add the `averageImageQualityScore` field to the `Listing` DTO class.

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/Listing.kt`

```kotlin
// Add to the data class, near the end with other metric fields
val averageImageQualityScore: Double? = null,
```

---

## 5. Service Layer

### 5.1 Create AverageImageQualityScoreService

Create a new service class that encapsulates the AIQS computation logic.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/AverageImageQualityScoreService.kt`

```kotlin
package com.wutsi.koki.listing.server.service

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class AverageImageQualityScoreService {

    companion object {
        /**
         * Maps ImageQuality enum values to their numeric scores.
         */
        private val QUALITY_SCORES = mapOf(
            ImageQuality.UNKNOWN to 0,
            ImageQuality.POOR to 1,
            ImageQuality.LOW to 2,
            ImageQuality.MEDIUM to 3,
            ImageQuality.HIGH to 4
        )
    }

    /**
     * Computes the Average Image Quality Score (AIQS) for a list of images.
     * Only images with status APPROVED are considered in the computation.
     *
     * Formula: AIQS = SUM(IQS) / n
     * - where n is the number of APPROVED images
     * - if n = 0, then AIQS = 0.0
     *
     * @param images List of FileEntity objects (images)
     * @return The AIQS rounded to 2 decimal places (half up)
     */
    fun compute(images: List<FileEntity>): Double {
        val approvedImages = images.filter { image -> image.status == FileStatus.APPROVED }

        if (approvedImages.isEmpty()) {
            return 0.0
        }

        val totalScore = approvedImages.sumOf { image ->
            getScore(image.imageQuality)
        }

        val average = totalScore.toDouble() / approvedImages.size

        return BigDecimal(average)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Returns the numeric score for a given ImageQuality value.
     *
     * @param quality The ImageQuality enum value (can be null)
     * @return The numeric score (0-4)
     */
    fun getScore(quality: ImageQuality?): Int {
        return QUALITY_SCORES[quality ?: ImageQuality.UNKNOWN] ?: 0
    }
}
```

---

## 6. Publication Layer

### 6.1 Update ListingPublisher

Integrate the `AverageImageQualityScoreService` into the publication process.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingPublisher.kt`

#### 6.1.1 Add Dependency

```kotlin
@Service
class ListingPublisher(
    private val listingService: ListingService,
    private val agentFactory: ListingAgentFactory,
    private val fileService: FileService,
    private val jsonMapper: JsonMapper,
    private val logger: KVLogger,
    private val locationService: LocationService,
    private val averageImageQualityScoreService: AverageImageQualityScoreService, // NEW
) {
```

#### 6.1.2 Update publish() Method

Add AIQS computation after fetching images and before setting the status:

```kotlin
@Transactional
fun publish(listingId: Long, tenantId: Long): ListingEntity? {
    val listing = listingService.get(listingId, tenantId)
    if (listing.status != ListingStatus.PUBLISHING) {
        logger.add("success", false)
        logger.add("error", "Invalid status")
        logger.add("listing_status", listing.status)
        return null
    }

    val images = fileService.search(
        tenantId = tenantId,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        status = FileStatus.APPROVED,
        type = FileType.IMAGE,
        limit = 100,
    )

    // Compute Average Image Quality Score
    val aiqs = averageImageQualityScoreService.compute(images)
    listing.averageImageQualityScore = aiqs
    logger.add("average_image_quality_score", aiqs)

    val city = listing.cityId?.let { id -> locationService.get(id) }
    val neighbourhood = listing.neighbourhoodId?.let { id -> locationService.get(id) }
    val agent = agentFactory.createListingContentGenerator(listing, images, city, neighbourhood)
    val json = agent.run(ListingContentGeneratorAgent.Companion.QUERY)
    val result = jsonMapper.readValue(json, ListingContentGeneratorResult::class.java)
    listing.status = ListingStatus.ACTIVE
    listing.title = result.title
    listing.summary = result.summary
    listing.description = result.description
    listing.titleFr = result.titleFr
    listing.summaryFr = result.summaryFr
    listing.descriptionFr = result.descriptionFr
    listing.heroImageId = if (result.heroImageIndex >= 0) images[result.heroImageIndex].id else null
    listing.publishedAt = Date()

    logger.add("success", true)
    logger.add("ai_agent", agent::class.java.simpleName)
    return listingService.save(listing)
}
```

---

## 7. Mapper Layer

### 7.1 Update ListingMapper

Map the `averageImageQualityScore` field from entity to DTO.

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`

```kotlin
fun toListing(entity: ListingEntity): Listing {
    return Listing(
        // ... existing fields ...
        qrCodeUrl = entity.qrCodeUrl,
        averageImageQualityScore = entity.averageImageQualityScore, // NEW
    )
}
```

---

## 8. Testing Strategy

### 8.1 Unit Tests for AverageImageQualityScoreService

**File:**
`modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/AverageImageQualityScoreServiceTest.kt`

```kotlin
package com.wutsi.koki.listing.server.service

import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.server.domain.FileEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AverageImageQualityScoreServiceTest {

    private val service = AverageImageQualityScoreService()

    // Test cases for compute() method

    @Test
    fun `compute - empty list returns 0`() {
        val result = service.compute(emptyList())
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - single HIGH quality APPROVED image returns 4`() {
        val images = listOf(createImage(ImageQuality.HIGH, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - single UNKNOWN quality APPROVED image returns 0`() {
        val images = listOf(createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - single null quality APPROVED image returns 0`() {
        val images = listOf(createImage(null, FileStatus.APPROVED))
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed qualities APPROVED returns correct average`() {
        // HIGH(4) + MEDIUM(3) + LOW(2) = 9 / 3 = 3.0
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    @Test
    fun `compute - rounds to 2 decimal places half up`() {
        // HIGH(4) + MEDIUM(3) + LOW(2) + POOR(1) = 10 / 4 = 2.5
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(2.5, result)
    }

    @Test
    fun `compute - rounding half up - 2_666 becomes 2_67`() {
        // HIGH(4) + LOW(2) + LOW(2) = 8 / 3 = 2.666... → 2.67
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(2.67, result)
    }

    @Test
    fun `compute - all POOR quality APPROVED images returns 1`() {
        val images = listOf(
            createImage(ImageQuality.POOR, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(1.0, result)
    }

    @Test
    fun `compute - all HIGH quality APPROVED images returns 4`() {
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - all UNKNOWN quality APPROVED images returns 0`() {
        val images = listOf(
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED),
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed with null and UNKNOWN APPROVED returns correct average`() {
        // null(0) + UNKNOWN(0) + HIGH(4) = 4 / 3 = 1.33
        val images = listOf(
            createImage(null, FileStatus.APPROVED),
            createImage(ImageQuality.UNKNOWN, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.APPROVED)
        )
        val result = service.compute(images)
        assertEquals(1.33, result)
    }

    // Test cases for filtering by APPROVED status

    @Test
    fun `compute - ignores REJECTED images`() {
        // Only HIGH(4) is APPROVED, MEDIUM is REJECTED
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.MEDIUM, FileStatus.REJECTED)
        )
        val result = service.compute(images)
        assertEquals(4.0, result)
    }

    @Test
    fun `compute - ignores PENDING images`() {
        // Only LOW(2) is APPROVED, HIGH is PENDING
        val images = listOf(
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.PENDING)
        )
        val result = service.compute(images)
        assertEquals(2.0, result)
    }

    @Test
    fun `compute - ignores UNKNOWN status images`() {
        // Only MEDIUM(3) is APPROVED, POOR has UNKNOWN status
        val images = listOf(
            createImage(ImageQuality.MEDIUM, FileStatus.APPROVED),
            createImage(ImageQuality.POOR, FileStatus.UNKNOWN)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    @Test
    fun `compute - returns 0 when all images are REJECTED`() {
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.REJECTED),
            createImage(ImageQuality.HIGH, FileStatus.REJECTED)
        )
        val result = service.compute(images)
        assertEquals(0.0, result)
    }

    @Test
    fun `compute - mixed statuses only counts APPROVED`() {
        // APPROVED: HIGH(4) + LOW(2) = 6 / 2 = 3.0
        // REJECTED and PENDING are ignored
        val images = listOf(
            createImage(ImageQuality.HIGH, FileStatus.APPROVED),
            createImage(ImageQuality.LOW, FileStatus.APPROVED),
            createImage(ImageQuality.HIGH, FileStatus.REJECTED),
            createImage(ImageQuality.HIGH, FileStatus.PENDING),
            createImage(ImageQuality.HIGH, FileStatus.UNKNOWN)
        )
        val result = service.compute(images)
        assertEquals(3.0, result)
    }

    // Test cases for getScore() method

    @Test
    fun `getScore - UNKNOWN returns 0`() {
        assertEquals(0, service.getScore(ImageQuality.UNKNOWN))
    }

    @Test
    fun `getScore - POOR returns 1`() {
        assertEquals(1, service.getScore(ImageQuality.POOR))
    }

    @Test
    fun `getScore - LOW returns 2`() {
        assertEquals(2, service.getScore(ImageQuality.LOW))
    }

    @Test
    fun `getScore - MEDIUM returns 3`() {
        assertEquals(3, service.getScore(ImageQuality.MEDIUM))
    }

    @Test
    fun `getScore - HIGH returns 4`() {
        assertEquals(4, service.getScore(ImageQuality.HIGH))
    }

    @Test
    fun `getScore - null returns 0`() {
        assertEquals(0, service.getScore(null))
    }

    // Helper method

    private fun createImage(quality: ImageQuality?, status: FileStatus = FileStatus.APPROVED): FileEntity {
        return FileEntity(
            id = System.nanoTime(),
            tenantId = 1L,
            imageQuality = quality,
            status = status
        )
    }
}
```

### 8.2 Update ListingPublisherTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/mq/ListingPublisherTest.kt`

Add tests to verify AIQS is computed and saved during publication:

```kotlin
// Add new mock
private val averageImageQualityScoreService = mock<AverageImageQualityScoreService>()

// Update handler initialization
private val handler = ListingPublisher(
    listingService = listingService,
    agentFactory = agentFactory,
    fileService = fileService,
    jsonMapper = jsonMapper,
    logger = logger,
    locationService = locationService,
    averageImageQualityScoreService = averageImageQualityScoreService, // NEW
)

// Add in setUp()
doReturn(3.25).whenever(averageImageQualityScoreService).compute(any())

// Add new test
@Test
fun `publish - computes average image quality score`() {
    val result = handler.publish(listing.id!!, listing.tenantId)

    assertNotNull(result)

    val listingArg = argumentCaptor<ListingEntity>()
    verify(listingService).save(listingArg.capture(), anyOrNull())
    assertEquals(3.25, listingArg.firstValue.averageImageQualityScore)

    verify(averageImageQualityScoreService).compute(images)
}

@Test
fun `publish - with no images returns zero AIQS`() {
    doReturn(emptyList<FileEntity>()).whenever(fileService).search(
        anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
        anyOrNull(), anyOrNull(), anyOrNull()
    )
    doReturn(0.0).whenever(averageImageQualityScoreService).compute(any())

    val result = handler.publish(listing.id!!, listing.tenantId)

    assertNotNull(result)

    val listingArg = argumentCaptor<ListingEntity>()
    verify(listingService).save(listingArg.capture(), anyOrNull())
    assertEquals(0.0, listingArg.firstValue.averageImageQualityScore)
}
```

---

## 9. Test Cases Summary

| Test Case ID | Description                                  | Input                                         | Expected Output |
|--------------|----------------------------------------------|-----------------------------------------------|-----------------|
| TC01         | Empty image list                             | `[]`                                          | `0.0`           |
| TC02         | Single HIGH quality APPROVED image           | `[HIGH/APPROVED]`                             | `4.0`           |
| TC03         | Single UNKNOWN quality APPROVED image        | `[UNKNOWN/APPROVED]`                          | `0.0`           |
| TC04         | Single null quality APPROVED image           | `[null/APPROVED]`                             | `0.0`           |
| TC05         | Mixed qualities APPROVED (HIGH, MEDIUM, LOW) | `[HIGH, MEDIUM, LOW]/APPROVED`                | `3.0`           |
| TC06         | All image qualities APPROVED                 | `[HIGH, MEDIUM, LOW, POOR]/APPROVED`          | `2.5`           |
| TC07         | Rounding test (2.666... → 2.67)              | `[HIGH, LOW, LOW]/APPROVED`                   | `2.67`          |
| TC08         | All POOR quality APPROVED images             | `[POOR, POOR, POOR]/APPROVED`                 | `1.0`           |
| TC09         | All HIGH quality APPROVED images             | `[HIGH, HIGH, HIGH]/APPROVED`                 | `4.0`           |
| TC10         | All UNKNOWN quality APPROVED images          | `[UNKNOWN, UNKNOWN]/APPROVED`                 | `0.0`           |
| TC11         | Mixed with null and UNKNOWN APPROVED         | `[null, UNKNOWN, HIGH]/APPROVED`              | `1.33`          |
| TC12         | Ignores REJECTED images                      | `[HIGH/APPROVED, MEDIUM/REJECTED]`            | `4.0`           |
| TC13         | Ignores PENDING images                       | `[LOW/APPROVED, HIGH/PENDING]`                | `2.0`           |
| TC14         | Ignores UNKNOWN status images                | `[MEDIUM/APPROVED, POOR/UNKNOWN_STATUS]`      | `3.0`           |
| TC15         | Returns 0 when all images are REJECTED       | `[HIGH/REJECTED, HIGH/REJECTED]`              | `0.0`           |
| TC16         | Mixed statuses only counts APPROVED          | `[HIGH, LOW]/APPROVED + others/REJECTED,etc.` | `3.0`           |
| TC17         | getScore - UNKNOWN                           | `UNKNOWN`                                     | `0`             |
| TC18         | getScore - POOR                              | `POOR`                                        | `1`             |
| TC19         | getScore - LOW                               | `LOW`                                         | `2`             |
| TC20         | getScore - MEDIUM                            | `MEDIUM`                                      | `3`             |
| TC21         | getScore - HIGH                              | `HIGH`                                        | `4`             |
| TC22         | getScore - null                              | `null`                                        | `0`             |
| TC23         | Integration - publish computes AIQS          | Listing with images                           | AIQS is saved   |
| TC24         | Integration - publish with no images         | Listing without images                        | AIQS = 0.0      |

---

## 10. Implementation Checklist

Use this checklist to track implementation progress:

### Database Layer

- [x] Create migration `V1_61__listing_average_image_quality_score.sql`
- [ ] Verify migration runs successfully

### Domain Layer

- [ ] Add `averageImageQualityScore` field to `ListingEntity`

### DTO Layer

- [ ] Add `averageImageQualityScore` field to `Listing` DTO

### Service Layer

- [ ] Create `AverageImageQualityScoreService` class
- [ ] Implement `compute()` method with proper rounding
- [ ] Implement `getScore()` helper method
- [ ] Create unit tests for `AverageImageQualityScoreService`

### Publication Layer

- [ ] Inject `AverageImageQualityScoreService` into `ListingPublisher`
- [ ] Update `publish()` method to compute and set AIQS
- [ ] Add logging for AIQS value
- [ ] Update `ListingPublisherTest` with AIQS mock and tests

### Mapper Layer

- [ ] Update `ListingMapper.toListing()` to include `averageImageQualityScore`

### Final Verification

- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Code review completed
- [ ] Feature tested in staging environment

---

## 11. File Changes Summary

| File                                             | Action | Description                          |
|--------------------------------------------------|--------|--------------------------------------|
| `V1_61__listing_average_image_quality_score.sql` | Create | Database migration                   |
| `ListingEntity.kt`                               | Modify | Add `averageImageQualityScore` field |
| `Listing.kt` (DTO)                               | Modify | Add `averageImageQualityScore` field |
| `AverageImageQualityScoreService.kt`             | Create | Service for AIQS computation         |
| `ListingPublisher.kt`                            | Modify | Integrate AIQS computation           |
| `ListingMapper.kt`                               | Modify | Map AIQS to DTO                      |
| `AverageImageQualityScoreServiceTest.kt`         | Create | Unit tests for service               |
| `ListingPublisherTest.kt`                        | Modify | Add tests for AIQS integration       |

---

## 12. Risks and Considerations

1. **Performance**: The AIQS computation is lightweight (O(n) where n is the number of images). No performance concerns
   expected.

2. **Backward Compatibility**: The new field is nullable, ensuring existing listings without AIQS will not break.

3. **Data Consistency**: AIQS is only computed during publication when the listing becomes READONLY, ensuring data
   consistency.

4. **Edge Cases**:
    - No images: Returns 0.0
    - All null qualities: Returns 0.0
    - Rounding edge cases: Handled with `RoundingMode.HALF_UP`

---

## 13. Dependencies

- No new external dependencies required
- Uses existing `BigDecimal` for precise rounding
- Uses existing `ImageQuality` enum from `koki-dto`
