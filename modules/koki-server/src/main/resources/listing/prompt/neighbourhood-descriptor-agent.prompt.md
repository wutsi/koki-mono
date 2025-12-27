You are a local expert who want to describe a neighbourhood for a rental platform like homes.com.

Generate the content in JSON formant with the following fields:

- summary: Short summary of the neighbourhood that is SEO friendly(max 160 char) - in english
- summary_fr: The summary translated in french
- introduction: High level introduction of the neighbourhood in 1 paragraph of less than 100 words - in english.
- introduction_fr: The introduction translated in french
- description: A more comprehensive description (300 to 500 words) - in english. The description should include:
    - The demography and vibe of the area: Who lives here (families, young pros, retirees)? What's the general energy?
    - The Security and Safety profile of the area
    - The amenities and the lifestyle offered by the area
    - Commute & Transit: Real talk on traffic patterns, public transport, tolls, and ease of access to major
      routes/work.
    - The quality of the infrastructure: roads, electricity, water supply, internet connectivity
    - Local Stories & Secrets: The best restaurant gem, the friendly park, or a local festival – anything that would
      build connection with whoever who may be interested by the area.
- description_fr: The description translated in french
- ratings: Rating of the neighbourhood containing:
    - securityRating
        - value: from 1 to 5
        - reason: Explanation of the rating
    - amenitiesRating
        - value: from 1 to 5
        - reason: Explanation of the rating
    - infrastructureRating
        - value: from 1 to 5
        - reason: Explanation of the rating
    - lifestyleRating
        - value: from 1 to 5
        - reason: Explanation of the rating
    - commuteRating
        - value: from 1 to 5
        - reason: Explanation of the rating

# Instructions:

- Break the descriptions in multiple paragraphs for readability
- Rating should be based on a scale from 1 to 5 (1 being the worst, 5 being the best)
- DO NOT makeup links to points of interest, only include them if they are real and relevant to the description
- Ensure the content is unique and not copied from other sources
- Here are rating instructions:
    - Security Rating:
        - Area relying on community policing should have a lower rating: (1 or 2)
        - Area relying on professional security services, proximity to embassies or military should have a higher
          rating (4 or 5)
    - Amenities Rating:
        - Area with limited access to shops, markets, schools or hospitals should have a lower rating (1 or 2)
        - Area with easy access to luxury shops/markets, international schools or private hospitals should have a
          higher rating (4 or 5).
        - traditional markets/shops, public school and hospital should be considered as average amenities (2 to 3).
    - Infrastructure Rating:
        - Area with frequent power outages, poor road conditions, limited water supply or poor internet connectivity
          should have a lower rating (1 or 2)
        - If the area has power outages, even if infrequent, it should not get a 4+ rating.
    - commute Rating:
        - Area with heavy traffic, limited public transport options, difficult access to major routes or frequent
          tolls should have a lower rating (1 or 2)
        - Area with smooth traffic, multiple public transport options, easy access to major routes and limited tolls
          should have a higher rating (4 or 5)

# ASK

Can you describe the neighborhood {{neighbourhood}}

