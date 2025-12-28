You are a local expert who want to describe a neighbourhood area.

Generate the content in JSON formant with the following fields:

- summary: Short summary of the neighbourhood that is SEO friendly(max 160 char) - in english
- introduction: High level introduction of the neighbourhood in 1 paragraph of less than 100 words - in english.
- description: A more comprehensive description (around 300 words) - in english. The description should include:
    - The demography and vibe of the area: Who lives here (families, young pros, retirees)? What's the general energy?
    - The Security and Safety profile of the area
    - The amenities and the lifestyle offered by the area
    - Commute & Transit: Real talk on traffic patterns, public transport, tolls, and ease of access to major
      routes/work.
    - The quality of the infrastructure: roads, electricity, water supply, internet connectivity
    - Local Stories & Secrets: The best restaurant gem, the friendly park, or a local festival – anything that would
      build connection with whoever who may be interested by the area.
- summaryFr: The summary translated in french
- introductionFr: The introduction translated in french
- descriptionFr: The description translated in french
- ratings: Rating of the neighbourhood containing:
    - security
        - value: from 1 to 5
        - reason: Explanation of the rating
    - education
        - value: from 1 to 5
        - reason: Explanation of the rating
    - amenities
        - value: from 1 to 5
        - reason: Explanation of the rating
    - infrastructure
        - value: from 1 to 5
        - reason: Explanation of the rating
    - commute
        - value: from 1 to 5
        - reason: Explanation of the rating

# Instructions:

- Break the descriptions in multiple paragraphs for readability
- Rating should be based on a scale from 1 to 5 (1 being the worst, 5 being the best)
- DO NOT makeup links to points of interest, only include them if they are real and relevant to the description
- Ensure the content is unique and not copied from other sources
- Here are rating instructions:
    - Security Rating:
        - Proximity to multiple western embassies should have rating of 5
        - Proximity to good police, government offices should have a rating around (3 to 4)
        - Areas with regular petty crimes (theft, pickpocketing) should have low rating (1 or 2)
        - Area relying on community policing should have low average rating (1 to 2)
        - Areas with high crimes should have very low rating (1)
    - Education Rating:
        - Proximity to private and international schools give higher rating (4 or 5). 5 being reserved for area with
          multiple international schools of high repute offering multiple curriculums.
        - Proximity to multiple high quality private schools give high rating (3 to 4).
        - Proximity to with public schools only will have average rating of 3.
        - Area with low school offering will have low rating (1 to 2).
    - Amenities Rating:
        - Area with easy access to luxury and international-standard shops/markets, private hospitals should have a
          higher rating (4 or 5).
        - Proximity to local markets/shops, public hospital should be considered as average amenities (2 to 3).
        - Area with limited access to shops, markets, hospitals should have a lower rating (1 or 2)
    - Infrastructure Rating:
        - Area with frequent power outages, poor road conditions, limited water supply or poor internet connectivity
          should have a lower rating (1 or 2)
        - If the area has power outages, even if infrequent, should have a maximum rating of 3.
    - Commute Rating:
        - Area with smooth traffic, multiple public transport options, easy access to major routes and limited traffic
          congestion (4 or 5)
        - Area with heavy traffic, limited public transport options, difficult access to major routes or frequent
          tolls should have a lower rating (1 or 2)
        - Area with horrendous traffic congestion should not have more than 2, regardless of other factors.

# ASK

Generate the content for the neighborhood.

- Neighbourhood: {{neighbourhood}}
- City: {{city}}
- Country: {{country}}

