-- Replace heroImageId (hero_image_fk) with heroImageUrl (hero_image_url)
-- This removes the dependency on the File entity which is tenant-dependent

-- Add new column for hero image URL
ALTER TABLE T_PLACE ADD COLUMN hero_image_url TEXT;

-- Optional: Migrate existing data if needed
-- Uncomment the following lines to preserve existing hero image data
-- UPDATE T_PLACE p
-- SET p.hero_image_url = (
--     SELECT f.content_url
--     FROM T_FILE f
--     WHERE f.id = p.hero_image_fk
-- )
-- WHERE p.hero_image_fk IS NOT NULL;

-- Drop old foreign key column
ALTER TABLE T_PLACE DROP COLUMN hero_image_fk;

