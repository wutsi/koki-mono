Place has the field heroImageId, that refer to a File entity which depends on a tenant.
We want to replace heroImageId to heroImageUrl, a list of hero images.

# Changes

1. In the database:
    - Add hero_image_url column to the places table. Use TEXT to store the URL.
    - Remove hero_image_fk field and associated foreign key constrain (if any)
2. In the Domain class
    - Update PlaceEntity to replace heroImageId with heroImageUrl: String
3. In the DTO
    - Update Place DTOs to replace heroImageId with heroImageUrl: String (in Place and PlaceSummary)
4. In Portal Public
    - Update PlaceModel and PlaceMapper to reflect the change from heroImageId to heroImageUrl
