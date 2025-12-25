We want to update the user schema to include information about the user's profile strength.
The profile strength will be represented as an integer value ranging from 0 to 100, indicating how complete the user's
profile is.
A higher value indicates a more complete profile.

The profile strength should be calculated based on the following criteria:

- Basic Information (20 points): Name, Email, Phone Number
- Profile Picture (20 points): Presence of a profile picture
- Social Media Links (20 points): The value depends on the number of social media links provided (up to 5 links:
  facebook, instagram, twitter, tiktok, youtube)
- Biography (20 points): A short biography or description provided by the user. The value depends on the length of the
  biography (up to 1000 characters)
- Address Information (10 points): City
- Category (10 points)

# Instructions

- Generate the report of the changes in target/<feature>/report.md file.
- Include in the report snippets of the modified code files with explanations.

# ASK

- In koki-dto:
    - Update the user schema to include the profile strength information. It should include:
        - The value (0..100)
        - The breakdown of the score by criteria
    - The profile information should be included only in the `User` DTO class
- In koki-server:
    - Create a service (and unit tests) to calculate the profile strength based on the criteria mentioned above.
    - Ensure that the profile strength is calculated and included whenever a user profile is retrieved.
- In koki-portal:
    - Update the home page to display the profile strength as a progress bar.
    - Include a tooltip or information icon that explains how the profile strength is calculated based on the criteria.

