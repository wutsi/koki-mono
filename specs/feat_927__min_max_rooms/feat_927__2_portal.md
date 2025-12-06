We want to refactor the portal to use the new parameters in the listing search functionality.
We want the portal to map the `bedrooms` and `bathrooms` strings to the new integer parameters `min-bedrooms`,
`max-bedrooms`, `min-bathrooms`, and `max-bathrooms`.
`bedrooms` and `bathrooms` are strings that can represent single values or ranges like "2+".

Refactor `ListingService.search()` in `koki-portal` to map the old parameters to the new ones when calling the SDK.

# Instructions

- Generate the report of the changes in target/<feature>/<step>/report.md file.
- Include in the report snippets of the modified code files with explanations.

ASK:

- Make the code changes for the step #3: Update in module koki-portal
- No need to change the test
