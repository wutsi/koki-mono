Add an endpoint to return listings similar to another one.

# API Endpoint

Endpoint: /v1/listings/LISTING_ID/similar

Headers:

- Tenant ID

Query Parameters:

- status - (Optional) Statuses of the similar properties
- same-agent - (Optional - default=false). If `true`, the listing return will have the same seller agent as the original
  listing.
- same-neighborhood - (Optional - default=false). If `true`, the listing return will ben in the same neighborhood as the
  original
- same-city - (Optional - defualt=false). If `true`, the listing return will ben in the same city as the original
  listing. Default=false
- limit - (Optional - default=10) Maximum number of similar listings to return. Max value is 50.

Response

- List of `ListingSimilaritySummary` sorted by score. Each item has the following field:
    - id: ID of the listing
    - score: 0..1

# Similarity Rules

Similar listings should respect the following rules:

- The listing type must be the same as the reference listing
- Prices within the same range: +/- 25%. For sold listings, use sales price
- number of rooms withing the same range: +/- 1 (not for LAND, COMMERCIAL or INDUSTRIAL listings)
- listing types similarity rules:
    - LAND is ONLY similar to LAND
    - COMMERCIAL is ONLY similar to COMMERCIAL
    - INDUSTRIAL is ONLY similar to INDUSTRIAL
    - for the other types are similar to any type that is not LAND, COMMERCIAL or INDUSTRIAL

# Similarity score

Apply the following algorithm for computing the similarity score:

```
FUNCTION ComputeSimilarity(PA, PB, W_price, W_beds, W_type)
    // --- 1. Type Score (Stype) ---
    IF PA.type == PB.type:
    S_type = 1.0
    ELSE:
    S_type = 0.0
    // NOTE: If S_type is 0, the final score will be low.
    // You may choose to STOP and return 0 immediately here if
    // type mismatch is a hard filter.

    // --- 2. Bedroom Score (Sbeds) ---
    beds_diff = ABS(PA.bedrooms - PB.bedrooms)
    IF beds_diff == 0:
    S_beds = 1.0
    ELSE IF beds_diff == 1:
    S_beds = 0.5
    ELSE:
    S_beds = 0.0

    // --- 3. Price Score (Sprice) ---
    price_A = PA.price
    price_B = PB.price

    // Check if within the hard ±25% boundary
    lower_bound = price_A * 0.75
    upper_bound = price_A * 1.25

    IF price_B < lower_bound OR price_B > upper_bound:
    S_price = 0.0
    ELSE:
    // Calculate a score that peaks at 1.0 (perfect match) and decays to 0.0 at the boundaries.
    // A simple normalized distance metric is usually sufficient:
    price_diff_norm = ABS(price_A - price_B) / (price_A * 0.25)
    S_price = 1.0 - price_diff_norm
    // Note: The linear decay model ensures S_price is 1.0 at perfect match (0% diff)
    // and 0.0 at the boundary (25% diff).


    // --- 4. Final Weighted Score ---
    Total_Score = (W_price * S_price) + (W_beds * S_beds) + (W_type * S_type)

    RETURN Total_Score
END FUNCTION
```

Use the following weights:

- Property Type: 50%
- Number of Bedrooms: 30%
- Price: 20%

# Additional Instructions

- Generate the report of the changes in target/<feature>/report.md file.

<!--
- Implement the similarity algorithm using strategy design pattern. We want to have the option of support in the
- future other similarity algorithms with minimal changes
- Define the class `ListingSimilarityService` for finding similar listings.
-->

# ASK

- Create the DTO classes in koki-dto module for the request and response of the new endpoint.
- Do not create request DTO since all parameters are passed as query parameters.

<!--
 - Make sure to include unit tests for the similarity algorithm and the API endpoint.
 - Update the documentation to reflect the new endpoint and its usage
-->
