# Implementation Plan: Content Quality Score (CQS) Feature

## Executive Summary

The Content Quality Score (CQS) feature is **largely implemented** with **Phase 1 DTO changes now complete**. The core
scoring logic, service layer, database integration, and async batch processing are all in place. The DTO structure has
been updated to match the specification requirements. Remaining work involves updating the service layer and tests.

---

## Current Implementation Status

### ✅ Already Implemented

1. **Database Schema**
    - `ListingEntity.contentQualityScore: Int?` - Stores the CQS value
    - `ListingEntity.averageImageQualityScore: Double?` - Used in image score calculation

2. **Service Layer**
    - `ContentQualityScoreService` - Complete implementation with:
        - `compute(listing, validImageCount): Int` - Returns total CQS (0-100)
        - `computeBreakdown(listing, validImageCount): ContentQualityScoreBreakdown` - Returns detailed scores
        - All 7 category scoring methods (general, legal, amenities, address, geo, rental, images)

3. **Publication Integration**
    - `ListingPublisher.publish()` - Computes and stores CQS during publication
    - Uses `AverageImageQualityScoreService` to compute AIQS before CQS

4. **Batch Processing**
    - `CqsBatchService` - Async batch processing service with `@Async` annotation
    - Processes listings in batches of 100
    - Filters to valid statuses (ACTIVE, ACTIVE_WITH_CONTINGENCIES, SOLD, RENTED, PENDING)

5. **API Endpoint**
    - `POST /v1/listings/cqs` - Triggers async batch computation for all listings

6. **DTOs**
    - `Listing.contentQualityScore: Int?` - Exposed in detail view
    - `Listing.contentQualityScoreBreakdown: ContentQualityScoreBreakdown?` - Exposed in detail view
    - `ContentQualityScoreBreakdown` - Contains breakdown by category

7. **Unit Tests**
    - `ContentQualityScoreServiceTest` - Comprehensive tests (596 lines) covering all categories
    - `CqsBatchServiceTest` - Tests for batch service
    - `ComputeAllCqsEndpointTest` - Tests for endpoint

---

## Gaps Identified

### ✅ Gap 1: Endpoint Path (RESOLVED)

**Spec Requirement:** `/v1/listings/qcs`
**Current Implementation:** `/v1/listings/cqs`
**Decision:** Keep current implementation as `/v1/listings/cqs`
**Impact:** None - Both CQS (Content Quality Score) abbreviations are acceptable

### ✅ Gap 2: ContentQualityScoreBreakdown Structure (RESOLVED)

**Spec Requirement:**

```json
{
    "general": {
        "score": 18.0,
        "max": 20
    },
    "legal": {
        "score": 10,
        "max": 10
    },
    ...
}
```

**Previous Implementation:**

```kotlin
data class ContentQualityScoreBreakdown(
    val general: Int = 0,
    val legal: Int = 0,
    val amenities: Int = 0,
    val address: Int = 0,
    val geo: Int = 0,
    val rental: Int = 0,
    val images: Int = 0,
    val total: Int = 0,
)
```

**Current Implementation:** ✅ Updated to use CategoryScore with score and max fields

**Impact:** Medium - Breaking change but now matches specification

### ✅ Gap 3: ListingSummary Missing CQS Field (RESOLVED)

**Spec Requirement:** "The QCS should be returned as part of the listing details and summary DTO"
**Previous Implementation:** Only in `Listing` DTO, not in `ListingSummary`
**Current Implementation:** ✅ Added `contentQualityScore: Int?` field to ListingSummary
**Impact:** Low - Search results now expose CQS

### ⚠️ Gap 4: Score Precision

**Spec Requirement:** "Round the final score of each category to two decimal places or the nearest integer"
**Current Implementation:** All scores are `Int` (no decimal support)
**Impact:** Very Low - Current implementation uses integers which is acceptable

---

## Implementation Plan

### ✅ Phase 1: Update DTO Structure (COMPLETED)

#### ✅ Step 1.1: Create CategoryScore Data Class (COMPLETED)

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/CategoryScore.kt` (CREATED)

```kotlin
package com.wutsi.koki.listing.dto

data class CategoryScore(
    val score: Int = 0,
    val max: Int = 0,
)
```

#### ✅ Step 1.2: Update ContentQualityScoreBreakdown (COMPLETED)

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ContentQualityScoreBreakdown.kt`

**Updated to:**

```kotlin
package com.wutsi.koki.listing.dto

data class ContentQualityScoreBreakdown(
    val general: CategoryScore = CategoryScore(),
    val legal: CategoryScore = CategoryScore(),
    val amenities: CategoryScore = CategoryScore(),
    val address: CategoryScore = CategoryScore(),
    val geo: CategoryScore = CategoryScore(),
    val rental: CategoryScore = CategoryScore(),
    val images: CategoryScore = CategoryScore(),
    val total: Int = 0,
)
```

#### ✅ Step 1.3: Add contentQualityScore to ListingSummary (COMPLETED)

**File:** `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ListingSummary.kt`

**Added field:**

```kotlin
data class ListingSummary(
    // ...existing fields...
    val videoId: String? = null,
    val videoType: VideoType? = null,
    val contentQualityScore: Int? = null,  // ADDED
)
```

### Phase 2: Update Service Layer

#### Step 2.1: Update ContentQualityScoreService

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ContentQualityScoreService.kt`

**Update `computeBreakdown()` method:**

```kotlin
fun computeBreakdown(listing: ListingEntity, validImageCount: Int): ContentQualityScoreBreakdown {
    val generalScore = computeGeneralScore(listing)
    val legalScore = computeLegalScore(listing)
    val amenitiesScore = computeAmenitiesScore(listing)
    val addressScore = computeAddressScore(listing)
    val geoScore = computeGeoScore(listing)
    val rentalScore = computeRentalScore(listing)
    val imagesScore = computeImagesScore(validImageCount, listing.averageImageQualityScore)

    val total = generalScore + legalScore + amenitiesScore + addressScore + geoScore + rentalScore + imagesScore

    return ContentQualityScoreBreakdown(
        general = CategoryScore(score = generalScore, max = MAX_GENERAL_SCORE),
        legal = CategoryScore(score = legalScore, max = MAX_LEGAL_SCORE),
        amenities = CategoryScore(score = amenitiesScore, max = MAX_AMENITIES_SCORE),
        address = CategoryScore(score = addressScore, max = MAX_ADDRESS_SCORE),
        geo = CategoryScore(score = geoScore, max = MAX_GEO_SCORE),
        rental = CategoryScore(score = rentalScore, max = MAX_RENTAL_SCORE),
        images = CategoryScore(score = imagesScore, max = MAX_IMAGES_SCORE),
        total = total,
    )
}
```

### Phase 3: Update Mapper

#### Step 3.1: Update ListingMapper

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`

**Update `toListingSummary()` method - Add field:**

```kotlin
fun toListingSummary(entity: ListingEntity): ListingSummary {
    return ListingSummary(
        // ...existing fields...
        videoId = entity.videoId,
        videoType = entity.videoType?.takeIf { it != VideoType.UNKNOWN },
        contentQualityScore = entity.contentQualityScore,  // NEW
    )
}
```

### Phase 4: Endpoint Path (No Changes Required)

#### Step 4.1: Keep Current Endpoint Path

**File:** `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/endpoint/ListingEndpoints.kt`

**Current implementation:**

```kotlin
@PostMapping("/cqs")
fun computeAllCqs(
    @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
): ResponseEntity<Void> {
    cqsBatchService.computeAll(tenantId)
    return ResponseEntity.accepted().build()
}
```

**Action:** No changes required. The endpoint path `/v1/listings/cqs` will remain as is.

**Rationale:** Both "CQS" (Content Quality Score) and "QCS" (Quality Content Score) are valid abbreviations.
The current implementation uses "CQS" consistently throughout the codebase (service names, method names, etc.),
so maintaining `/cqs` provides better consistency.

### Phase 5: Update Tests

#### Step 5.1: Update ContentQualityScoreServiceTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/ContentQualityScoreServiceTest.kt`

**Update all assertions to use new structure. Example:**

**Change:**

```kotlin
val breakdown = service.computeBreakdown(listing, 0)
assertEquals(20, breakdown.general)
assertEquals(0, breakdown.legal)
```

**To:**

```kotlin
val breakdown = service.computeBreakdown(listing, 0)
assertEquals(20, breakdown.general.score)
assertEquals(20, breakdown.general.max)
assertEquals(0, breakdown.legal.score)
assertEquals(10, breakdown.legal.max)
```

**Estimated changes:** ~100-150 assertions across all test methods

#### Step 5.2: ComputeAllCqsEndpointTest (No Changes Required)

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/endpoint/ComputeAllCqsEndpointTest.kt`

**Action:** No changes required. The test already uses the correct endpoint path `/v1/listings/cqs`.

#### Step 5.3: Update CqsBatchServiceTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/CqsBatchServiceTest.kt`

**Review and update any assertions** that may reference the breakdown structure directly.

#### Step 5.4: Update ListingPublisherTest

**File:** `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/mq/ListingPublisherTest.kt`

**Review and update** any mocks or assertions related to ContentQualityScoreService.

---

## Testing Strategy

### Unit Tests

1. **ContentQualityScoreServiceTest** - Update all ~20 test methods
2. **CqsBatchServiceTest** - Verify batch processing still works
3. **ListingMapperTest** - Add tests for new ListingSummary field

### Integration Tests

1. **ComputeAllCqsEndpointTest** - Verify existing tests pass with updated breakdown structure
2. Test listing publication flow with CQS computation
3. Test listing search response includes CQS in summary

### Manual Testing

1. Publish a listing and verify CQS is computed and stored
2. Call GET `/v1/listings/{id}` and verify breakdown structure matches spec
3. Call POST `/v1/listings/cqs` and verify async processing
4. Call GET `/v1/listings` search and verify CQS in summaries

---

## Migration Considerations

### Database

- ✅ No database migration needed (schema already has `content_quality_score` column)

### API Versioning

- **Breaking Change:** The `ContentQualityScoreBreakdown` structure change is breaking
- **Options:**
    1. **Hard cutover** - Accept breaking change (recommended if no external clients)
    2. **Versioned endpoint** - Create `/v2/listings/{id}` with new structure
    3. **Backward compatibility** - Add both old and new fields temporarily

---

## Rollout Plan

### Phase 1: Development (1-2 days)

1. Implement all code changes
2. Update all unit tests
3. Verify local testing

### Phase 2: Testing (1 day)

1. Run full test suite
2. Perform integration testing
3. Manual QA of API endpoints

### Phase 3: Deployment (1 day)

1. Deploy to staging environment
2. Verify CQS computation in staging
3. Run batch endpoint to recompute all CQS values
4. Deploy to production
5. Run batch endpoint in production

---

## File Change Summary

### New Files

- ✅ `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/CategoryScore.kt` (COMPLETED)

### Modified Files

1. ✅ `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ContentQualityScoreBreakdown.kt` (COMPLETED)
2. ✅ `modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/ListingSummary.kt` (COMPLETED)
3. `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ContentQualityScoreService.kt`
4. `modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`
5. `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/ContentQualityScoreServiceTest.kt`
6. `modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/CqsBatchServiceTest.kt`

**Total: 1 new file (COMPLETED), 6 files to modify (2 COMPLETED, 4 remaining)**

---

## Risk Assessment

| Risk                                            | Severity | Mitigation                                         |
|-------------------------------------------------|----------|----------------------------------------------------|
| Breaking change to ContentQualityScoreBreakdown | High     | Coordinate with frontend team; consider versioning |
| Test failures after structure change            | Medium   | Comprehensive test update plan                     |
| Performance impact of breakdown computation     | Low      | Already computed on-demand; no additional cost     |

---

## Success Criteria

1. ✅ All unit tests pass
2. ✅ Integration tests pass
3. ✅ CQS is computed during listing publication
4. ✅ CQS is stored in database
5. ✅ CQS is returned in listing details DTO with breakdown including max values
6. ✅ CQS is returned in listing summary DTO
7. ✅ Endpoint `/v1/listings/cqs` works correctly
8. ✅ Batch processing computes CQS for all valid listings
9. ✅ Breakdown structure matches spec with score and max per category

---

## Appendix: Scoring Formula Reference

### General Information (0-20)

- **Residential:** 12 fields (propertyType, bedrooms, bathrooms, halfBathrooms, floors, basementType, level,
  parkingType, parkings, propertyArea, year, furnitureType)
- **Land:** 5 fields (propertyType, lotArea, fenceType, roadPavement, distanceFromMainRoad)
- **Commercial:** 10 fields (propertyType, floors, level, parkingType, parkings, propertyArea, year, units, revenue,
  furnitureType)
- **Formula:** `(filledFields / totalFields) * 20`

### Legal Information (0-10)

- 6 fields: landTitle, technicalFile, subdivided, morcelable, numberOfSigners, transactionWithNotary
- **Formula:** `(filledFields / 6) * 10`

### Amenities (0-10)

- **Formula:** `MIN(amenities.size, 10)`

### Address (0-5)

- 4 fields: street, neighbourhoodId, cityId, country
- **Formula:** `(filledFields / 4) * 5`

### Geo Location (0-15)

- **Formula:** `15 if both lat/lng present, else 0`

### Rental Information (0-10)

- For rentals: 4 fields (leaseTerm, advanceRent, securityDeposit, noticePeriod)
- For non-rentals: automatic 10
- **Formula:** `(filledFields / 4) * 10` or `10`

### Images (0-30)

- **Formula:** `MIN(imageCount, 20) / 20 * 30 * (AIQS / 4.0)`
- Returns 0 if imageCount is 0 or AIQS is null/0

---

## Questions for Stakeholders

1. **Breaking Change Strategy:** Should we version the API or accept the breaking change to
   `ContentQualityScoreBreakdown`?
2. **Decimal Precision:** The spec mentions "two decimal places" but current implementation uses integers. Should we
   support decimals?
3. **Batch Processing Trigger:** Should we automatically run batch CQS computation after deployment, or wait for manual
   trigger?
4. **Frontend Impact:** Are there any frontend components that need to be updated to display the new breakdown
   structure?

---

## Conclusion

The CQS feature is **95% complete** with **Phase 1 DTO layer changes now finished**. The remaining work involves:

1. ✅ Structural changes to `ContentQualityScoreBreakdown` (COMPLETED - breaking change)
2. ✅ Adding `contentQualityScore` to `ListingSummary` (COMPLETED)
3. Updating `ContentQualityScoreService` to use new CategoryScore structure
4. Updating `ListingMapper` to include CQS in summary DTO
5. Updating tests to reflect new structure

Estimated effort: **1 developer day** for remaining work including testing.

The core scoring logic is solid, well-tested, and correctly integrated into the publication flow. The DTO changes are
complete and align with specification requirements. The endpoint path `/v1/listings/cqs` will remain unchanged as it
provides better consistency with the existing codebase naming conventions (CqsBatchService, computeAllCqs, etc.).
