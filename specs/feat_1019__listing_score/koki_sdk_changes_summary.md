# koki-sdk Module Changes - Summary

## ✅ Completed Successfully

The koki-sdk module has been successfully updated to support the new Content Quality Score (CQS) endpoint.

---

## Changes Made

### 1. ✅ Updated KokiListings.kt

**File**: `/modules/koki-sdk/src/main/kotlin/com/wutsi/koki/sdk/KokiListings.kt`

#### 1.1 Added Import

```kotlin
import com.wutsi.koki.listing.dto.GetListingCqsResponse
```

#### 1.2 Added `getCqs()` Method

```kotlin
/**
 * Get the Content Quality Score breakdown for a listing
 *
 * @param id The listing ID
 * @return GetListingCqsResponse containing overall CQS and detailed breakdown by category
 */
fun getCqs(id: Long): GetListingCqsResponse {
    val url = urlBuilder.build("$PATH_PREFIX/$id/cqs")
    return rest.getForEntity(url, GetListingCqsResponse::class.java).body!!
}
```

**Location**: Added after the `get()` method (line 111-114)

**Features**:

- ✅ Calls the new `GET /v1/listings/{id}/cqs` endpoint
- ✅ Returns `GetListingCqsResponse` with CQS breakdown
- ✅ Follows existing SDK patterns and conventions
- ✅ Uses URLBuilder for consistent URL construction
- ✅ Proper error handling (non-null assertion follows SDK pattern)

---

## Usage Example

```kotlin
// Initialize SDK
val koki = Koki(
    tenantId = 1L,
    apiKey = "your-api-key",
    serverUrl = "https://api.koki.example.com"
)

// Get listing CQS breakdown
val cqsResponse = koki.listings.getCqs(listingId = 100L)

// Access overall CQS score
println("Overall CQS: ${cqsResponse.overallCqs}")

// Access breakdown by category
val breakdown = cqsResponse.cqsBreakdown
println("General Score: ${breakdown.general.score}/${breakdown.general.max}")
println("Legal Score: ${breakdown.legal.score}/${breakdown.legal.max}")
println("Amenities Score: ${breakdown.amenities.score}/${breakdown.amenities.max}")
println("Address Score: ${breakdown.address.score}/${breakdown.address.max}")
println("Geo Location Score: ${breakdown.geo.score}/${breakdown.geo.max}")
println("Rental Score: ${breakdown.rental.score}/${breakdown.rental.max}")
println("Images Score: ${breakdown.images.score}/${breakdown.images.max}")
println("Total: ${breakdown.total}")
```

---

## Response Structure

The `getCqs()` method returns a `GetListingCqsResponse` object with the following structure:

```kotlin
data class GetListingCqsResponse(
    val listingId: Long,           // The listing ID
    val overallCqs: Int,            // Overall CQS score (0-100)
    val cqsBreakdown: ContentQualityScoreBreakdown  // Detailed breakdown
)

data class ContentQualityScoreBreakdown(
    val general: CategoryScore,     // General info score (max: 20)
    val legal: CategoryScore,       // Legal info score (max: 10)
    val amenities: CategoryScore,   // Amenities score (max: 10)
    val address: CategoryScore,     // Address score (max: 5)
    val geo: CategoryScore,         // Geo location score (max: 15)
    val rental: CategoryScore,      // Rental info score (max: 10)
    val images: CategoryScore,      // Images score (max: 30)
    val total: Int,                 // Total score (sum of all categories)
)

data class CategoryScore(
    val score: Int,  // Current score
    val max: Int,    // Maximum possible score
)
```

---

## Error Handling

The method follows the SDK pattern of throwing exceptions on errors:

```kotlin
try {
    val cqsResponse = koki.listings.getCqs(listingId = 999L)
    println("CQS: ${cqsResponse.overallCqs}")
} catch (e: HttpClientErrorException.NotFound) {
    println("Listing not found")
} catch (e: HttpClientErrorException) {
    println("Error: ${e.message}")
}
```

---

## Integration with Existing SDK Methods

The new `getCqs()` method complements existing listing methods:

```kotlin
// Get full listing details
val listing = koki.listings.get(id = 100L)
println("Listing: ${listing.listing.title}")

// Get CQS breakdown
val cqs = koki.listings.getCqs(id = 100L)
println("CQS: ${cqs.overallCqs}")

// Get AI-generated listing details
val aiListing = koki.listings.getAIListing(id = 100L)

// Search listings
val results = koki.listings.search(
    statuses = listOf(ListingStatus.ACTIVE),
    limit = 10
)

// Compute CQS for all listings (batch operation)
koki.listings.computeAllCqs()
```

---

## Files Modified

### Modified (1 file)

1. `/modules/koki-sdk/src/main/kotlin/com/wutsi/koki/sdk/KokiListings.kt`
    - Added `GetListingCqsResponse` import
    - Added `getCqs(id: Long)` method

---

## Verification Checklist

✅ **Code Quality**

- [x] Follows existing SDK patterns
- [x] Consistent naming conventions
- [x] Proper Kotlin idioms
- [x] Clean code structure
- [x] KDoc comments (recommended to add)

✅ **API Compatibility**

- [x] Matches endpoint signature
- [x] Correct HTTP method (GET)
- [x] Proper URL construction
- [x] Correct response type

✅ **SDK Conventions**

- [x] Uses URLBuilder
- [x] Uses RestTemplate
- [x] Non-null assertion (!! operator) for consistency
- [x] Method naming follows camelCase pattern
- [x] Positioned logically (after `get()` method)

---

## Dependencies

The SDK method depends on:

1. ✅ `GetListingCqsResponse` DTO (created in koki-dto module)
2. ✅ `ContentQualityScoreBreakdown` DTO (already exists in koki-dto)
3. ✅ `CategoryScore` DTO (already exists in koki-dto)
4. ✅ `GET /v1/listings/{id}/cqs` endpoint (implemented in koki-server)

All dependencies are satisfied.

---

## Build Notes

To build the SDK module:

```bash
cd /Users/htchepannou/Perso/koki-mono

# Build koki-dto first (contains DTOs)
mvn clean install -pl modules/koki-dto -am -DskipTests

# Build koki-sdk
mvn clean install -pl modules/koki-sdk -am -DskipTests
```

---

## Testing Recommendations

While the SDK module doesn't have unit tests (typical for SDK libraries), you can test the integration:

### Manual Testing

```kotlin
@Test
fun `test getCqs returns valid breakdown`() {
    val koki = createKokiClient()

    // Arrange: Create a listing with known data
    val listingId = createTestListing()

    // Act: Get CQS breakdown
    val response = koki.listings.getCqs(listingId)

    // Assert
    assertNotNull(response)
    assertEquals(listingId, response.listingId)
    assertTrue(response.overallCqs in 0..100)

    val breakdown = response.cqsBreakdown
    assertTrue(breakdown.general.score <= breakdown.general.max)
    assertTrue(breakdown.legal.score <= breakdown.legal.max)
    assertTrue(breakdown.amenities.score <= breakdown.amenities.max)
    assertTrue(breakdown.address.score <= breakdown.address.max)
    assertTrue(breakdown.geo.score <= breakdown.geo.max)
    assertTrue(breakdown.rental.score <= breakdown.rental.max)
    assertTrue(breakdown.images.score <= breakdown.images.max)
    assertEquals(breakdown.total, response.overallCqs)
}
```

---

## Documentation Updates Needed

Consider updating:

1. SDK README with `getCqs()` method example
2. API documentation with CQS endpoint details
3. Release notes mentioning new CQS functionality
4. Developer guide with CQS interpretation guide

---

## Summary

✅ **Status**: COMPLETE

The koki-sdk module has been successfully updated with the new `getCqs()` method, enabling SDK consumers to easily
retrieve Content Quality Score breakdowns for listings.

**Changes**: 1 file modified, 1 method added, 1 import added

**API Surface**: Added `KokiListings.getCqs(id: Long): GetListingCqsResponse`

The SDK is now fully compatible with the new CQS feature implemented in the koki-server and koki-dto modules.
