We want the system to scrape webpages of real estate listings and import their listings.

# Domain:

The domain is webscraping.
This domain has the following entities:

## Website

This is the entity representing a website to scrape listings from. It has the following attributes:

- id: Long - ID of the website
- tenantId: Long - Tenant ID for multi-tenant support
- userId: Long - ID of the owner of the website
- baseUrl: String - Base URL of the website (not null)
- baseUrlHash: String - MD5 hash of the base URL (not null - unique per tenant)
- listingUrlPrefix: String - URL prefix for listings (not null)
- contentSelector: String? - CSS selector to extract listing content
- imageSelector: String? - CSS selector to extract listing images
- createdAt: Date - Timestamp of when the website was added
- active: Boolean - Whether the website is active for scraping

## Webpage

This entity represents a page that has been scraped from a website. It has the following attributes:

- id: Long - ID of the scraped page
- tenantId: Long - Tenant ID for multi-tenant support
- websiteId: Long - userID of the website
- url: String - URL of the scraped page (not null)
- urlHash: String - MD5 hash of the URL for uniqueness (not null - unique per tenant)
- content: String - Extracted content from the page (nullable)
- images: List<String> - List of image URLs extracted from the page
- createdAt: Date - Timestamp of when the page was scraped

# API

We want to expose the following endpoints:

- POST /v1/websites:
    - Description: Add a new website to scrape listings from.
    - Request body: The body should be the DTO `CreateWebsiteRequest` with the following fields:
        - userId: Long - userID of the agent
        - baseUrl: String - Base URL of the website
        - listingUrlPrefix: String - URL prefix for listings
        - contentSelector: String - CSS selector to extract listing content
        - imageSelector: String - CSS selector to extract listing images
        - active: Boolean - Whether the website is active for scraping
    - Response body: The response should be the DTO `CreateWebsiteResponse` with the following field:
        - websiteId: Long - ID of the created website

- POST /v1/websites/{id}:
    - Description: Update a website to scrape listings from.
    - Request body: The body should be the DTO `UpdateWebsiteRequest` with the following fields:
        - listingUrlPrefix: String - URL prefix for listings
        - contentSelector: String - CSS selector to extract listing content
        - imageSelector: String - CSS selector to extract listing images
        - active: Boolean - Whether the website is active for scraping

- POST /v1/websites/{id}/scrape:
    - Description: Scrape listings from the specified website.
    - Response body: The body should be the DTO `ScrapeWebsiteResponse` with the following field:
        - webpageImported: Number of webpages scraped

- GET /v1/webpages
    - Description: Search webpages
    - Request parameters:
        - website-id: Long? - ID of the website
        - active: Boolean? - Filter by active status
        - limit: Int - Number of webpages to retrieve (default=20)
        - offset: Int - Offset for pagination (default=0)
    - Response body: The body should be the DTO `SearchWebpagesResponse` with the following field:
        - webpages: List of `WebpageSummary` DTOs, each containing:
            - id: Long - ID of the webpage
            - websiteId: Long - ID of the website
            - url: String - URL of the webpage
            - active: Boolean - Whether the webpage is active for scraping
            - createdAt: Date - Timestamp of when the page was scraped

- GET /v1/webpages/{id}
    - Description: Retrieve a webpage by ID
    - Response body: The body should be the DTO `GetWebpageResponse` with the following field:
        - webpage: an instance of  `Webpage` DTO containing:
            - id: Long - ID of the webpage
            - websiteId: Long - ID of the website
            - url: String - URL of the webpage
            - content: String - Extracted content from the page
            - images: List<String> - List of image URLs extracted from the page
            - active: Boolean - Whether the webpage is active for scraping
            - createdAt: Date - Timestamp of when the page was scraped
