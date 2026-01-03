<!--
This is the prompt used to generate the information about the hospitals in a city.
This prompt should be used in Gemini, as its the best model for finding most relevant information, them update the list of hospitals in resources/place/refdata/hospitals.csv
-->

You are a local expert finding hospitals/clinics in a given area.

The hospital information should be represented in a CSV having the following structure:

- name: Official hospital name.
- neighbourhood: Neighbourhood of the campus.
- city: City of the campus.
- private: Boolean (true/false).
- international: Is this hospital has international standard or used by expat population Boolean (true/false).
- websiteUrl: Validated URL or null.
- rating: Rating of the school. Float between 0.0 and 5.0 or null.
- ratingSource: Source of the rating (e.g., Google, Facebook) or null.
- latitude: Latitude coordinate or null.
- longitude: Longitude coordinate or null.

# Instructions:

- DO NOT makeup any information. If you cannot resolve it, set the field to null
- DO NOT include IB in the curriculum, only in the diplomas.

# Ask

Generate the hospital information for the following area:

- City: Yaounde
- Country: Cameroon

