# Goal

We want to build a feature for adding a video to a listing.
Video are a powerful medium to get attention of visitors.

# Core Feature and Logic

For now, we don't want to upload video, but we want to add links to the video that real-estate agent have on their
social media pages.
The platform that we want to support are

- Tiktok
- YouTube
- Instagram

## How would it work

- The real estate agent open the listing from the admin portal
- He click on the link to link a video
- He opens the video from social media and copy the link
- ... paste the link in the admin portal and save
- Now the listing is associate with the video that will be visible on public listing

# Technical Requirements

## Domain Layer

- We want to store for each listing a  `videoId` and `videoType` in the `ListingEntity`
    - `videoId`: ID of the video on social media. Should be stored as `String` (max 36 characters)
    - `videoType`: will be one of following enum value: TIKTOK, YOUTUBE and INSTAGRAM. It should be persisted as INT (as
      all other enums)

## Service Layer

- We want to create a `VideoURLParser` service for parsing video URL to extract the videoId and videoType from URL
    - YouTube URL Format: https://www.youtube.com/watch?v=VIDEO_ID or https://youtu.be/VIDEO_ID
    - TikTok URL Format: https://www.tiktok.com/USER/video/VIDEO_ID
    - Instagram URL Format: https://www.instagram.com/reels/VIDEO_ID or https://www.instagram.com/p/VIDEO_ID
- Note that video URL can include query parameters (Ex:  https://youtu.be/VIDEO_ID?utm_source=xxx&play=1)
- Ensure the parser handles (strips) trailing slashes when extracting the videoId
- We want to use strategy design pattern so that we have 1 implementation of `VideoURLParser` per type, so that we can
  easily extend the platform to support other type of videos.

## API Layer

- We want to add the endpoint for link the video
    - The endpoint:  `/v1/listings/video`
    - The request fields:
        - videoUrl: String (REQUIRED)
    - This endpoint should return HTTP status code `409` with the error code LISTING_VIDEO_NOT_SUPPORTED if the video
      URL is not supported
    - If the video already set, this endpoint will overwrite it.

## DTO Layer

- `ListingSummary` should have the following additional fields:
    - videoId
    - videoType
- `Listing` should have the following additional fields:
    - videoId
    - videoType
    - videoEmbedUrl.
        - The embed format should be
            - YouTube: https://www.youtube.com/embed/VIDEO_ID
            - TikTok: https://www.tiktok.com/player/v1/VIDEO_ID
            - Instagram: https://www.instagram.com/p/VIDEO_ID/embed/
        - This field will to be persisted in the database, but computed based on `videoId` and `videoType`

## SDK Layer

- The endpoint for linking the video should be added into the listing SDK

# Boundaries & Constraints

- 1 video per listing
- We support only videos from TIKTOK, INSTAGRAM and YOUTUBE
