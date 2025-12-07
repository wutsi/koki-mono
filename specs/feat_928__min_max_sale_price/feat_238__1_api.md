We want to update the Listing Search API so that we can search by sale price range.

We want to:

- Update koki-server to accept the new parameters: `min-sale-price` and `max-sale-price` as optional long parameters.
- Update koki-sdk to reflect the new parameters in the API client.

# Instructions

- Generate the report of the changes in target/<feature>/report.md file.
- Include in the report snippets of the modified code files with explanations.
- Make the changes on the /v1 version of the API.

ASK:

- Make the code changes in koki-server and koki-sdk modules.
- Update `SearchListingEndpointTest` to cover the new parameters.
