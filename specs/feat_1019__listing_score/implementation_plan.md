# Implementation Plan: Content Quality Score (CQS) Feature

## Executive Summary

This implementation plan outlines the changes needed to complete the Content Quality Score (CQS) feature. After
analyzing the codebase, I found that **most of the feature has already been implemented**. The following changes are
still required:

### What's Already Implemented ✅

1. **Domain Layer**: `ListingEntity.contentQualityScore` field exists
2. **DTO Layer**: `contentQualityScore` added to `Listing` and `ListingSummary` DTOs
3. **DTO Layer**: `ContentQualityScoreBreakdown` and `CategoryScore` DTOs exist
4. **Service Layer**: `ContentQualityScoreService` fully implemented with all scoring logic
5. **Service Layer**: CQS computation on publish (in `ListingPublisher`)
6. **Batch Processing**: `CqsBatchService` with `@Async` support exists
7. **Endpoint**: `POST /v1/listings/cqs` already implemented

### What's Missing ❌

1. **Endpoint**: `GET /v1/listings/{id}/cqs` - not yet implemented
2. **Service Layer**: CQS update on listing create/update operations
3. **Mapper**: Currently computes breakdown on-the-fly in `Listing` DTO (needs cleanup)

---

## Detailed Implementation Plan

### Phase 1: DTO Layer Changes ✅ COMPLETED

#### 1.1 Create `GetListingCqsResponse.kt` ✅

**Location**: `/modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/GetListingCqsResponse.kt`

**Purpose**: Response DTO for the `GET /v1/listings/{id}/cqs` endpoint

**Content**:

```kotlin
package com.wutsi.koki.listing.dto

data class GetListingCqsResponse(
    val listingId: Long,
    val overallCqs: Int,
    val cqsBreakdown: ContentQualityScoreBreakdown,
)
```

**Notes**:

- ✅ **COMPLETED**: File created at
  `/modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/GetListingCqsResponse.kt`
- The spec shows `overallCqs` as a double (85.5), but the implementation uses `Int`
- Keep as `Int` for consistency with existing `contentQualityScore` field
- The breakdown structure already exists and matches the spec

#### 1.2 Update `Listing.kt` ✅

**Location**: `/modules/koki-dto/src/main/kotlin/com/wutsi/koki/listing/dto/Listing.kt`

**Change**: Remove `contentQualityScoreBreakdown` field (line 96)

**Reason**: According to spec: "The `Listing` DTO should not include the CQS breakdown by category, as this information
is only relevant for the `GET /v1/listings/{id}/cqs` endpoint."

**Impact**: This is a breaking change but aligns with the spec requirements

**Status**: ✅ **COMPLETED** - Field removed from Listing.kt

---

### Phase 2: API Endpoint Implementation

#### 2.1 Add `GET /v1/listings/{id}/cqs` endpoint

**Location**: `/modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/endpoint/ListingEndpoints.kt`

**Implementation**:

```kotlin
@GetMapping("/{id}/cqs")
fun getListingCqs(
    @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
    @PathVariable id: Long,
): GetListingCqsResponse {
    val listing = service.get(id, tenantId)

    // Compute breakdown on-the-fly
    val images = fileService.search(
        tenantId = tenantId,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        status = FileStatus.APPROVED,
        type = FileType.IMAGE,
        limit = 100,
    )

    val breakdown = contentQualityScoreService.computeBreakdown(listing, images.size)

    return GetListingCqsResponse(
        listingId = listing.id ?: -1,
        overallCqs = breakdown.total,
        cqsBreakdown = breakdown,
    )
}
```

**Dependencies**:

- Add `fileService` injection (already exists in endpoint)
- Add `contentQualityScoreService` injection (need to add)

**Notes**:

- Computes breakdown on-the-fly as per spec
- Uses existing service methods

---

### Phase 3: Service Layer Enhancements

#### 3.1 Update CQS on Listing Create

**Location**: `/modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingService.kt`

**Method**: `create(request: CreateListingRequest, tenantId: Long)`

**Change**: Add CQS computation after creating listing

**Implementation**:

```kotlin
// After dao.save(ListingEntity(...))
// Compute initial CQS
val images = fileService.search(
    tenantId = tenantId,
    ownerId = listing.id,
    ownerType = ObjectType.LISTING,
    status = FileStatus.APPROVED,
    type = FileType.IMAGE,
    limit = 100,
)
listing.contentQualityScore = contentQualityScoreService.compute(listing, images.size)
listing.averageImageQualityScore = averageImageQualityScoreService.compute(images)
```

**Note**: For newly created listings, image count will typically be 0, so initial CQS will be low

#### 3.2 Update CQS on Listing Update Operations

**Location**: `/modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingService.kt`

**Methods to Update** (all need CQS recomputation):

1. `update(id, request, tenantId)` - line ~270
2. `amenities(id, request, tenantId)` - affects amenities score
3. `address(id, request, tenantId)` - affects address score
4. `geoLocation(id, request, tenantId)` - affects geo score
5. `price(id, request, tenantId)` - doesn't affect CQS directly
6. `leasing(id, request, tenantId)` - affects rental score
7. `legalInfo(id, request, tenantId)` - affects legal score

**Implementation Pattern** (add before final save):

```kotlin
// Recompute CQS
val images = fileService.search(
    tenantId = tenantId,
    ownerId = listing.id,
    ownerType = ObjectType.LISTING,
    status = FileStatus.APPROVED,
    type = FileType.IMAGE,
    limit = 100,
)
listing.contentQualityScore = contentQualityScoreService.compute(listing, images.size)
```

**Optimization Note**: Consider creating a helper method to avoid code duplication:

```kotlin
private fun updateCqs(listing: ListingEntity, tenantId: Long) {
    val images = fileService.search(
        tenantId = tenantId,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        status = FileStatus.APPROVED,
        type = FileType.IMAGE,
        limit = 100,
    )
    listing.contentQualityScore = contentQualityScoreService.compute(listing, images.size)
    listing.averageImageQualityScore = averageImageQualityScoreService.compute(images)
}
```

#### 3.3 Dependencies to Add

**Location**: `/modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/service/ListingService.kt`

Add to constructor:

- `fileService: FileService`
- `contentQualityScoreService: ContentQualityScoreService`
- `averageImageQualityScoreService: AverageImageQualityScoreService`

---

### Phase 4: Mapper Layer Adjustments

#### 4.1 Remove CQS Breakdown Computation from Mapper

**Location**: `/modules/koki-server/src/main/kotlin/com/wutsi/koki/listing/server/mapper/ListingMapper.kt`

**Changes**:

1. Remove `computeCqsBreakdown()` method (lines 133-145)
2. Remove `contentQualityScoreBreakdown` from `toListing()` method (line 126)
3. Remove `fileService` dependency from mapper (no longer needed)
4. Remove `contentQualityScoreService` dependency from mapper (no longer needed)

**Result**: Simpler mapper focused only on entity-to-DTO conversion

---

### Phase 5: Event Handling (Already Implemented)

The following event handlers already update CQS appropriately:

- ✅ `ListingFileUploadedEventHandler` - updates AIQS which affects CQS
- ✅ `ListingFileDeletedEventHandler` - updates AIQS which affects CQS
- ✅ `ListingPublisher.publish()` - computes CQS on publish

**Note**: These handlers indirectly update CQS through AIQS changes. No changes needed.

---

## Implementation Order & Dependencies

### Step 1: DTO Layer ✅ COMPLETED (No Dependencies)

1. ✅ Create `GetListingCqsResponse.kt`
2. ✅ Remove `contentQualityScoreBreakdown` from `Listing.kt`

### Step 2: Mapper Layer (Depends on Step 1)

1. Remove `computeCqsBreakdown()` method
2. Update `toListing()` method
3. Remove unused dependencies

### Step 3: Service Layer (Depends on Steps 1-2)

1. Add dependencies to `ListingService` constructor
2. Create `updateCqs()` helper method
3. Update `create()` method
4. Update all update methods (`update`, `amenities`, `address`, etc.)

### Step 4: API Layer (Depends on Steps 1-3)

1. Add `contentQualityScoreService` to `ListingEndpoints` constructor
2. Add `GET /v1/listings/{id}/cqs` endpoint

---

## Testing Strategy

### Unit Tests

#### 4.1 Service Layer Tests

**File**: Create
`/modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/service/ContentQualityScoreServiceTest.kt` (if
doesn't exist)

**Test Cases**:

1. ✅ Already tested via `CqsBatchServiceTest`
2. Test each component score calculation separately:
    - `computeGeneralScore()` for Residential, Land, Commercial
    - `computeLegalScore()`
    - `computeAmenitiesScore()`
    - `computeAddressScore()`
    - `computeGeoScore()`
    - `computeRentalScore()` for rental and non-rental
    - `computeImagesScore()` with various image counts and AIQS values

#### 4.2 Endpoint Tests

**File**: Create
`/modules/koki-server/src/test/kotlin/com/wutsi/koki/listing/server/endpoint/GetListingCqsEndpointTest.kt`

**Test Cases**:

1. GET CQS for existing listing - returns breakdown
2. GET CQS for non-existent listing - returns 404
3. GET CQS for listing in different tenant - returns 404
4. GET CQS breakdown values match computation

#### 4.3 Integration Tests

**Test Cases**:

1. Create listing → verify CQS computed
2. Update listing → verify CQS updated
3. Add amenities → verify amenities score increases
4. Add address → verify address score increases
5. Add geo location → verify geo score increases
6. Update to rental → verify rental score calculated
7. Publish listing → verify CQS recomputed

---

## Verification of Spec Requirements

### ✅ Core Feature and Logic

- [x] CQS values from 0-100
- [x] General Information scoring (0-20) - by property category
- [x] Legal Information scoring (0-10)
- [x] Amenities scoring (0-10)
- [x] Address scoring (0-5)
- [x] Geo Location scoring (0-15)
- [x] Rental Information scoring (0-10)
- [x] Images scoring (0-30) - with AIQS integration

### ✅/❌ CQS Endpoints

- [x] `POST /v1/listings/cqs` - already implemented
- [❌] `GET /v1/listings/{id}/cqs` - **needs implementation**

### ✅/❌ Service Layer

- [❌] CQS computed on create - **needs implementation**
- [❌] CQS computed on update - **needs implementation**
- [x] CQS computed on publish - already implemented
- [x] Separate `ContentQualityScoreService` - already exists
- [x] Unit testing - exists via `CqsBatchServiceTest`

### ✅ Domain Layer

- [x] CQS stored in database (`contentQualityScore` field)
- [x] Mapped to `ListingEntity.contentQualityScore`

### ✅/❌ DTO Layer

- [x] `contentQualityScore` in `Listing` DTO
- [x] `contentQualityScore` in `ListingSummary` DTO
- [❌] `Listing` should NOT include breakdown - **needs fix**

### ✅ Boundaries & Constraints

- [x] `@Async` for batch processing - implemented in `CqsBatchService`

---

## Risk Assessment

### High Risk

**Breaking Change**: Removing `contentQualityScoreBreakdown` from `Listing` DTO

- **Impact**: API consumers relying on this field will break
- **Mitigation**:
    - Mark field as deprecated first
    - Release in separate version
    - Document migration path to new endpoint

### Medium Risk

**Performance Impact**: Computing CQS on every update

- **Impact**: Additional database queries (images) on each update
- **Mitigation**:
    - Cache image count in listing entity (`totalImages` already exists)
    - Only recompute when relevant fields change
    - Consider async update for non-critical paths

### Low Risk

**Database Load**: Batch CQS computation

- **Impact**: Already using `@Async` and batching
- **Mitigation**: Already handled via batch size of 100

---

## Database Migration

**Not Required**: The `contentQualityScore` field already exists in the database schema.

---

## Code Quality Checklist

- [ ] All new code follows Kotlin coding standards
- [ ] All methods have KDoc comments
- [ ] All public APIs have validation
- [ ] Error handling follows existing patterns
- [ ] Logging added for important operations
- [ ] Unit tests achieve >80% coverage
- [ ] Integration tests cover happy and sad paths
- [ ] No hardcoded values (use constants)
- [ ] Null safety handled properly

---

## Deployment Plan

### Phase 1: Non-Breaking Changes

1. Create `GetListingCqsResponse` DTO
2. Add `GET /v1/listings/{id}/cqs` endpoint
3. Update service layer to compute CQS on create/update
4. Deploy and verify

### Phase 2: Breaking Changes (Optional - if strict spec compliance needed)

1. Deprecate `contentQualityScoreBreakdown` in `Listing` DTO
2. Communicate to API consumers
3. Wait for migration period
4. Remove field in major version bump

---

## Estimated Effort

| Task                                     | Effort         | Priority |
|------------------------------------------|----------------|----------|
| Create `GetListingCqsResponse` DTO       | 15 min         | High     |
| Add `GET /v1/listings/{id}/cqs` endpoint | 1 hour         | High     |
| Add CQS to create/update operations      | 2-3 hours      | High     |
| Update mapper (remove breakdown)         | 30 min         | Medium   |
| Write endpoint tests                     | 2 hours        | High     |
| Write integration tests                  | 2 hours        | Medium   |
| Code review and fixes                    | 1 hour         | High     |
| **Total**                                | **~8-9 hours** |          |

---

## Open Questions

1. **Breaking Change Decision**: Should we remove `contentQualityScoreBreakdown` from `Listing` DTO immediately or
   deprecate first?
    - **Recommendation**: Keep for backward compatibility, document that new endpoint should be used

2. **Performance Optimization**: Should we cache image count to avoid repeated queries?
    - **Recommendation**: Use existing `totalImages` field but need to ensure it's kept in sync

3. **Batch vs Real-time**: Should CQS updates on create/update be synchronous or async?
    - **Recommendation**: Keep synchronous for immediate feedback, use batch for bulk corrections

4. **AIQS Update**: Should we also update AIQS on create/update, or only on publish?
    - **Recommendation**: Only on publish (existing behavior is correct)

---

## Conclusion

The CQS feature is **~80% complete**. The remaining work focuses on:

1. Adding the `GET /v1/listings/{id}/cqs` endpoint
2. Ensuring CQS is updated during listing create and update operations
3. Minor cleanup to align mapper with spec requirements

The implementation is well-structured and follows Spring Boot best practices. The main challenge is deciding how to
handle the breaking change to the `Listing` DTO.
