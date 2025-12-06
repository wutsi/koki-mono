We want to refactor the Listing Search API.
Currently, the `bedrooms` and `bathrooms` parameters are strings that accept single values or ranges like "2+".
We want to refactor the API so that

- it accepts `min-bedrooms`, `max-bedrooms`, `min-bathrooms`, and `max-bathrooms` as integer parameters, all optional.
- removes `bedrooms` and `bathrooms` parameters

We want to:

- Update koki-server to accept the new parameters.
- Update koki-sdk to reflect the new parameters in the API client.
- Update koki-portal to use the new parameters in the listing search functionality.

# Instructions

- Generate the report of the changes in target/<feature>/<step>/report.md file.
- Include in the report snippets of the modified code files with explanations.
- Make the changes on the /v1 version of the API.
- Before compiling the code, run the command `ktlint -F` to fix any code style issues.

ASK:

- Make the code changes for the step #1: Update in module koki-server
- Make the code changed for the step #2: Update in module koki-sdk
