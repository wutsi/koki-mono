We want to update listing to add information about legal info for each listing.
This information will help users understand the legal aspects of each listing.

# Domain Information

The legal information to add into the listing object are:

- land title: Is there a land title for the property? (boolean)
- technical file: Is there a technical file for the property? (boolean)
- number of signers: How many signers are there for the property? (integer)
- mutation type: TOTAL or PARTIAL (enum)
- transaction with notary: Is the transaction done with a notary? (boolean)

# The DTO

Add the legal information into the Listing DTO.
No not add them into the summary version of the Listing DTO.

# The API

Add the an endpoint for updating the legal information of a listing:

POST /v1/listings/{id}/legal-info: Update the legal information of a listing. The request body should contain the legal
information fields.

# The DB

Update the listing table to add the legal information fields
