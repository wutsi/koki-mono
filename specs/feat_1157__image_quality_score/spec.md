# Goal

We want to build a feature that compute the Average Image Quality Score (AIQS) for each listing.
This score evaluates the quality of the images uploaded by the users to a listing.

# Core Feature and Logic

Each image uploaded has a quality enumerated value assigned by the AI avent that validates all image uploaded. The
values are as follows: UNKNOWN, POOR, LOW, MEDIUM, HIGH.

We can compute the Image Quality Score (IQS) of each image as follows:

- UNKNOWN: score of 0
- POOR: score of 1
- LOW: score of 2
- MEDIUM: score of 3
- HIGH: score of 4

## Logic Precision

- The AIQS is a double value between 0 and 4, where 0 means that all images are of UNKNOWN quality, and 4 means that all
  images are of HIGH quality.
- **Formula:** AIQS = SUM(IQS) / n - where n is the number of APPROVED images in the listing. If n=0, then AIQS=0.
- **Precision:** The AIQS should be rounded to 2 decimal places, half up (e.g., 2.345 becomes 2.35, 2.344 becomes 2.34).

# Technical Requirements

- The AIQS is computed during the publication of the listing. After the publication, the listing become READONLY, and
  the AIQS should not be updated anymore.
- AIQS should be stored in the database for each listing.
- The AIQS should be returned as part of the listing details DTO.
- Create a separate service that computes the AIQS for a listing, which can be called during the publication process.
- Use unit testing to validate the correctness of the AIQS computation logic.

# Boundaries & Constraints

- The image is represented in the domain layer by the class `FileEntity`
- The image quality is represented by the field `FileEntity.imageQuality`
- The AIQS will not be displayed in the UI
