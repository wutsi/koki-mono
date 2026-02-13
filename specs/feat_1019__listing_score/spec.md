# Goal

We want to build a feature that compute a Content Quality Score (QCS) for each listing.
This score will be used to separate high-quality listings from low-quality ones, and to improve the overall user
experience on our platform.

# Core Feature and Logic

The QCS values is from 0 to 100, where 0 is the lowest quality and 100 is the highest quality.
The score will be calculated based on the completeness and accuracy of the listing information, as well as the quality
of the images, videos and documents provided.
The Content Quality Score (QCS) will be calculated based on several factors:

- General Information
- Legal Informations
- Amenities
- Address
- Geo Location
- Rental Information
- Images
- Video Tour
- Documents

## General Information (0-10)

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

## Legal Information (0-10)

The value is determined by the availability of the following information:

- Land title?
- Technical file?
- Subdivided?
- Morcelable?
- Number of signers
- Transaction with Notary?

## Address (0-5)

The value is determined by the availability of the following information:

- Street name
- Neighbourhood
- City
- Country

## Geo Location (0-15)

- 5 if both latitude and longitude are provided
- 0 if either latitude or longitude is missing

## Rental information (0-10)

- This apply only for rental listings, and is determined by the availability of the following information:
    - Rental terms
    - Advance payment
    - Security deposit
    - Notice period
- For no rental listings, the score will be 10.

## Images (0-20)

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
Image Score = (MAX(Number of Images, 20) / 20) * 20 * (Average Image Quality Score / 4.0)
```

## Video Tour (0-10)

The availability of video tour will contribute to the score as follows:

- If a video tour is provided, the score will be 10.
- If no video tour is provided, the score will be 0.

## Documents (0-20)

The score depends on the documents provided.
The document required for each listing type are as follows:

- Land Title
- The ID card of the property owner
- The Technical File

# Tech Stack and Architecture

# Technical Requirements

# Boundaries & Constraints


