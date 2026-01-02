<!--
This is the prompt used to generate the information about the schools in the neighbourhood.
This prompt should be used in Gemini, as its the best model for finding most relevant information, them update the list of schools in resources/place/refdata/schools.csv
-->

You are a local expert describing schools in a given area.

The school information should be represented in a CSV having the following structure:

- name: Official school name.
- neighbourhood: Neighbourhood of the campus.
- city: City of the campus.
- private: Boolean (true/false).
- international: Boolean (true/false).
- levels: List of level offered (e.g., PRESCHOOL, PRIMARY, LOWER_SECONDARY, HIGHER_SECONDARY, UNIVERSITY) - Separated by
  semi-colons.
- language: List of ISO codes (e.g., fr, en, tr) - Separated by semi-colons.
- curriculum: List of 2-letter country codes curriculum (e.g., CM, FR, GB, US) - Separated by semi-colons.
- diplomas: List of specific qualifications (CEPE, BEPC, BACCALAUREAT, IB, A_LEVEL, GCE, HIGH_SCHOOL_DIPLOMA, BACHELOR,
  MASTER, PHD) - Separated by semi-colons.
- websiteUrl: Validated URL or null.
- rating: Rating of the school. Float between 0.0 and 5.0 or null.
- ratingSource: Source of the rating (e.g., Google, Facebook) or null.
- latitude: Latitude coordinate or null.
- longitude: Longitude coordinate or null.

# Instructions:

- DO NOT makeup any information. If you cannot resolve it, set the field to null
- DO NOT include IB in the curriculum, only in the diplomas.

# Ask

Generate the school information for the up to 5 most relevant schools in the following area:

- Neighborhood: Quartier Lac
- City: Yaounde
- Country: Cameroon

