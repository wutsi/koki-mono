We want to create a new domain for managing the content displayed in different places.
These places can be:

- Neighborhoods
- Schools
- Parcs
- etc.

This will allow us to have neighborhood pages with rich and curated content.

# The Domain objects

The content for each Place will have the following informations:

- id: Unique identifier of the place content (long)
- heroImageId: Unique identifier of the hero image (long)
- name: Name of the place (max 100 char)
- ascii_name: Name of the place with ascii characters only. Used for keyword search (max 100 char) - This is not
  serialized to the DTO.
- type: Type of the place. One of the following enum values: NEIGHBORHOOD, SCHOOL, PARK,
- status: Status of the place. One of the following enum values: DRAFT, PUBLISHING, PUBLISHED, ARCHIVED
- summary: Short summary of the neighbourhood that is SEO friendly(max 160 char) - in english
- summary_fr: The summary translated in french
- introduction: High level introduction of the neighbourhood in 1 paragraph of less than 100 words - in english.
- introduction_fr: The introduction translated in french
- description: A more comprehensive description (300 to 500 words)
- description_fr: The description translated in french
- neighbourhoodId: ID of the neighborhood associated with the place.
- longitude
- latitude
- websiteURL: URL of the website of the place
- phoneNumber: Contact phone number of the place
- Here are additional informations specific to schools:
    - private? true | false
    - international? true | false
    - diplomas: List of diploma code obtained in the school. Should be from following enum values: CEPE, BEPC,
      BACCALAUREAT, IB, A_LEVEL, HIGH_SCHOOL_DIPLOMA, GCE, ABI, CAP, BTS, BACHELOR, MASTER, MBA, PHD, OTHER
    - languages: List of languages of instruction. Use 2 letter code
    - academicSystems: List of countries of the academic system provided by the school. Use 2 letter code of the country
    - faith: CATHOLIC, PROTESTANT, MUSLIM, JEWISH, ISLAMIC, ORTHODOX, OTHER
    - levels: List of levels provided by the school. One of the following enum values: PRESCHOOL, PRIMARY,
      SECONDARY, HIGHER_EDUCATION, OTHER

- ratings: Rating of the neighbourhood containing - 1..5 ratings.
- ratingCriteria: Each place rating will depends on different criteria. Each rating criteria will be managed by the
  entity RatingCriteria having the following structure:
    - criteria: it's an enum from the following list: SECURITY, AMENITIES, INFRASTRUCTURE, LIFESTYLE, COMMUTE
    - value: from 1 to 5
    - reason: Explanation of the rating

The summary version of Place will contain only the following fields:

- id
- heroImageId
- neighborhoodId
- type
- name
- name_fr
- summary
- summary_fr
- rating

# The Domain Module

We should register the new domain PLACE in ObjectType enum.

- The ID of this module should be 302.
- The permissions associated with this domain are:
    - place:read
    - place:manage
    - place:delete
    - place:full_access

# The Domain Errors

The following errors should be defined for the Place domain:

- PLACE_NOT_FOUND: When a place with the given ID does not exist

# The API

We want to create a REST API to manage the Place content. This API will have the following endpoints:

- POST /v1/places: Create the content of a place. The content will be generated based on the place type
  and name and neighborhoodId.
- POST /v1/places/{id}: Regenerate the content of a place The content will be generated based on the place type
  and name and neighborhoodId.
- GET /v1/places/{id}: Retrieve a place by its ID.
- GET /v1/places: Retrieve a list of places with optional filters:
    - neighborhoodId: Filter by neighborhood ID (list)
    - type: Filter by place type (list)
    - status: Filter by place status (list)
    - keyword: Search places by keyword in name
- DELETE /v1/places/{id}: Delete a place by its ID.

# Instructions

- Implement soft delete for the Place entity
- For the AI content generation, return placeholder text for now. We will integrate a real AI service later.
- For each enum type, always add a UNKNOWN value as the first enum value to handle unexpected values.
- How to map enum fields:
    - at DTO layer, always use the enum type
    - At the API layer, always use the enum type
    - at Entity layer, always use the enum type. They will be stored as INTEGER in the DB in the DB
    - In DB, always define them as INTEGER
- Ho to map list of enum fields:
    - at DTO layer, always use List<EnumType>
    - at API layer, always use List<EnumType>
    - at Entity layer, always use String and store them as comma separated values in the DB
    - In DB, always define them as comma separated VARCHAR
- Store name as VARCHAR(255) in the DB, as the length my differ
- Store summary, introduction, description as TEXT in the DB, as the length my differ
- For the endpoint for searching places, Do not create a request body. All filters should be passed as query parameters.
- In the API, do not check permissions for now.

# Ask
