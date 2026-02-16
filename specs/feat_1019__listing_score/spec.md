# Goal

We want to build a feature that compute a Content Quality Score (QCS) for each listing.
This score will be used to separate high-quality listings from low-quality ones, and to improve the overall user
experience on our platform.

# Core Feature and Logic

The QCS values is from 0 to 100, where 0 is the lowest quality and 100 is the highest quality.
The score will be calculated based on the completeness and accuracy of the listing information, as well as the quality
of the images provided.
The Content Quality Score (QCS) will be calculated based on several factors:

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

- The QCS is computed during the publication of the listing.
- QCS should be stored in the database for each listing.
- The QCS should be returned as part of the listing details and summary DTO.
- The QCS breakdown by category (General Information, Legal Information, Amenities, Address, Geo Location, Rental
  Information, Images) should NOT be sored in the DB, but should be returned as part of the
  listing details DTO.
- The QCS breakdown should include the following information for each category:
    - The score for the category
    - The maximum possible score for the category
    - This is the proposed schema for the QCS breakdown in the listing details DTO:

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
    "images": {
        "score": 25.5,
        "max": 30
    },
    ...
}
```

- Create a separate service that computes the CQS for a listing, which can be called during the publication process.
- Use unit testing to validate the correctness of the CQS computation logic.
- Create an endpoint to compute asynchronously the CQS for all valid listings stored in the DB.
- Round the final score of each category to two decimal places or the nearest integer.

# Boundaries & Constraints

- The listing is represented in the domain layer by the class `ListingEntity`
- The listing publishing is handled by `ListingPublisher`
- The endpoint for computing the QCS of all listings should be /v1/listings/cqs
- Use springboot `@Async` annotation for running all the listings QCS

