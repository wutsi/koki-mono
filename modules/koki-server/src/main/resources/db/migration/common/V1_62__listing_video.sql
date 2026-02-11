-- Add video fields to listing table
ALTER TABLE T_LISTING
    ADD COLUMN video_id   VARCHAR(36),
    ADD COLUMN video_type INT;
