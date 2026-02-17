# koki-portal CQS Integration - Summary of Changes

## Overview

Updated the koki-portal module to support Content Quality Score (CQS) functionality as per the implementation plan.

## Changes Made

### 1. Model Layer - New Files Created

#### CategoryScoreModel.kt

- **Path**: `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/model/CategoryScoreModel.kt`
- **Purpose**: Portal model for representing a category score with score and max values
- **Fields**:
    - `score: Int` - The actual score achieved
    - `max: Int` - The maximum possible score

#### ContentQualityScoreBreakdownModel.kt

- **Path**:
  `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/model/ContentQualityScoreBreakdownModel.kt`
- **Purpose**: Portal model for the complete CQS breakdown by category
- **Fields**:
    - `general: CategoryScoreModel` - General information score
    - `legal: CategoryScoreModel` - Legal information score
    - `amenities: CategoryScoreModel` - Amenities score
    - `address: CategoryScoreModel` - Address score
    - `geo: CategoryScoreModel` - Geo location score
    - `rental: CategoryScoreModel` - Rental information score
    - `images: CategoryScoreModel` - Images score
    - `total: Int` - Overall CQS total

#### GetListingCqsResponseModel.kt

- **Path**: `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/model/GetListingCqsResponseModel.kt`
- **Purpose**: Portal model for the CQS response
- **Fields**:
    - `listingId: Long` - The listing ID
    - `overallCqs: Int` - The overall CQS score
    - `cqsBreakdown: ContentQualityScoreBreakdownModel` - The detailed breakdown

### 2. Model Layer - Updated Files

#### ListingModel.kt

- **Path**: `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/model/ListingModel.kt`
- **Change**: Added `contentQualityScore: Int?` field to the data class
- **Purpose**: Store the CQS value in the listing model for display in the portal

### 3. Mapper Layer

#### ListingMapper.kt

- **Path**: `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/mapper/ListingMapper.kt`
- **Changes**:
    1. Updated `toListingModel(entity: Listing, ...)` to map `contentQualityScore`
    2. Updated `toListingModel(entity: ListingSummary, ...)` to map `contentQualityScore`
    3. Added `toGetListingCqsResponseModel()` method to convert DTO to portal model
    4. Added `toContentQualityScoreBreakdownModel()` helper method
    5. Added `toCategoryScoreModel()` helper method

### 4. Service Layer

#### ListingService.kt

- **Path**: `/modules/koki-portal/src/main/kotlin/com/wutsi/koki/portal/listing/service/ListingService.kt`
- **Change**: Added `getCqs(id: Long)` method
- **Purpose**: Fetch the CQS breakdown for a specific listing
- **Implementation**: Calls `koki.getCqs(id)` from the SDK and converts the response to portal model

### 5. Test Fixtures

#### ListingFixtures.kt

- **Path**: `/modules/koki-portal/src/test/kotlin/com/wutsi/koki/portal/ListingFixtures.kt`
- **Changes**:
    1. Added `contentQualityScore = 85` to the main `Listing` fixture
    2. Added `contentQualityScore` to all `ListingSummary` fixtures:
        - First listing (id 1115, ACTIVE): 85
        - Second listing (id 1116, DRAFT): 72
        - Third listing (id 1117, PUBLISHING): 90
        - Fourth listing (id 1117, ACTIVE, LAND): 68
    3. Added `cqsBreakdown` fixture with complete breakdown structure
    4. Added `cqsResponse` fixture combining listing ID, overall score, and breakdown

## Dependencies

The implementation relies on:

- **koki-sdk**: `KokiListings.getCqs(id: Long)` method (already implemented)
- **koki-dto**:
    - `ContentQualityScoreBreakdown` DTO
    - `CategoryScore` DTO
    - `GetListingCqsResponse` DTO
    - Updated `Listing` and `ListingSummary` DTOs with `contentQualityScore` field

## Usage Example

```kotlin
// In a controller or service
val listingService: ListingService = ...

// Get a listing with CQS
val listing = listingService.get(id = 1115)
println("CQS: ${listing.contentQualityScore}") // Output: CQS: 85

// Get detailed CQS breakdown
val cqsResponse = listingService.getCqs(id = 1115)
println("Overall CQS: ${cqsResponse.overallCqs}") // Output: Overall CQS: 85
println("General Score: ${cqsResponse.cqsBreakdown.general.score}/${cqsResponse.cqsBreakdown.general.max}")
println("Images Score: ${cqsResponse.cqsBreakdown.images.score}/${cqsResponse.cqsBreakdown.images.max}")
```

## Testing

Test data is available in `ListingFixtures`:

- Use `ListingFixtures.listing` for a complete listing with CQS = 85
- Use `ListingFixtures.cqsBreakdown` for a sample breakdown
- Use `ListingFixtures.cqsResponse` for a complete response model

## Notes

- The CQS field is nullable (`Int?`) to handle listings where CQS has not yet been computed
- The portal models mirror the DTO structure for clean separation of concerns
- The mapper handles the conversion between DTOs and portal models
- All changes are backward compatible (adding new fields, not removing existing ones)
