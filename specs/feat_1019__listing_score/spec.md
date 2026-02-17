# Goal

We want to build a feature that compute a Content Quality Score (CQS) for each listing.
This score will be used to separate high-quality listings from low-quality ones, and to improve the overall user
experience on our platform.

# Core Feature and Logic

The CQS values is from 0 to 100, where 0 is the lowest quality and 100 is the highest quality.
The score will be calculated based on the completeness and accuracy of the listing information, as well as the quality
of the images provided.
The Content Quality Score (CQS) will be calculated based on several factors:

- General Information
- Legal Information
- Amenities
- Address
- Geo Location
- Rental Information
- Images

## General Information (0-20)

The value is determined by the availability of the following information:

- For Residential listing
    - Number of bedrooms
    - Number of bathrooms
    - Lot area
    - Property area
    - Parking Type
    - Number of parking spaces
    - Fence Type
    - Distance from main road
    - Road pavement type
    - Availability date
    - Year of construction
- For Land listing
    - Lot area
    - Distance from main road
    - Fence Type
    - Road pavement type
    - Availability date
- For Commercial listing
    - Number of units
    - Lot area
    - Property area
    - Parking Type
    - Number of parking spaces
    - Fence Type
    - Distance from main road
    - Road pavement type
    - Availability date
    - Year of construction

Since the number of fields is different and do not have weight, the score will be calculated linearly as follows:

```
General Information Score = (Number of Available Fields / Total Number of Fields) * 20
```

Field is available if it is not null and not empty (for string fields).

## Legal Information (0-10)

The value is determined by the availability of the following information:

- Land title?
- Technical file?
- Subdivided?
- Morcelable?
- Number of signers
- Transaction with Notary?

Since the number of fields is different and do not have weight, the score will be calculated linearly (like in General
Information).

## Amenities (0-10)

The formula for calculating the amenities score is:

```
Amenities Score = MIN(Number of Listing Amenities, 10)
```

## Address (0-5)

The value is determined by the availability of the following information:

- Street name
- Neighbourhood
- City
- Country

## Geo Location (0-15)

- 15 if both latitude and longitude are provided
- 0 if either latitude or longitude is missing

## Rental information (0-10)

- This apply only for rental listings, and is determined by the availability of the following information:
    - Rental terms
    - Advance payment
    - Security deposit
    - Notice period
- For no rental listings, the score will be 10.

## Images (0-30)

The score depends on 2 factors:

- The number of images provided. 20+ images will get the max score of 30, while 0 images will get a score of 0. The
  score will increase linearly with the number of images.
- The average quality of the images (float value). Each image is assigned a quality score having the following values:
    - UNKNOWN: score of 0
    - POOR: score of 1
    - LOW: score of 2
    - MEDIUM: score of 3
    - HIGH: score of 4

So, the formula for calculating the image score is:

```
Image Score = (MIN(Number of Images, 20) / 20) * 30 * (Average Image Quality Score / 4.0)
```

If Number of Images is 0 OR Average Image Quality Score is 0 or null, then Image Score is 0

# Technical Requirements

## CQS Endpoints

- Endpoint: `POST /v1/listings/cqs`
    - Description: This endpoint will trigger the computation of the CQS for all valid listings stored in the database.
      The computation should be done asynchronously to avoid blocking the main thread.
    - Request: None

- Endpoint: `GET /v1/listings/{id}/cqs`
    - Description: This endpoint will return the CQS breakdown by category for a specific listing. The breakdown should
      be computed on the fly when the endpoint is called, and should include the score and maximum possible score for
      each category.
    - Request: None
    - Response: A JSON object containing the CQS breakdown by category, as shown in the proposed schema above. The
      response class is `ListingCqsResponseDTO` that return the format shown below:
      ```json
      {
          "listingId": "12345",
          "overallCqs": 85.5,
          "cqsBreakdown": {
              "general": {
              "score": 18.0,
              "maxScore": 20.0
              },
              "legal": {
              "score": 8.0,
              "maxScore": 10.0
              },
              "amenities": {
              "score": 9.0,
              "maxScore": 10.0
              },
              "address": {
              "score": 4.5,
              "maxScore": 5.0
              },
              "geoLocation": {
              "score": 15.0,
              "maxScore": 15.0
              },
              "rentalInformation": {
              "score": 10.0,
              "maxScore": 10.0
              },
              "images": {
              "score": 25.5,
              "maxScore": 30.0
              }
          }
      }
      ```
      The `cqsBreakdown` field contains the score and maximum possible score for each category.

## Service Layer

- The CQS is computed and updated whenever a listing is created, updated, and published.
- Create a separate service that computes the CQS for a listing.
- Use unit testing to validate the correctness of the CQS computation logic.

## Domain Layer

- CQS should be stored in the database for each listing
- CQS should be mapped to the `ListingEntity.contentQualityScore`

## DTO Layer

- The `contentQualityScore` field should be added to the following the `Listing` and `ListingSummary` DTOs, and should
  be returned in the listing details and summary responses.
- The `Listing` DTO should not include the CQS breakdown by category, as this information is only relevant for the
  `GET /v1/listings/{id}/cqs` endpoint.

# Boundaries & Constraints

- Use springboot `@Async` annotation for running all the listings CQS

