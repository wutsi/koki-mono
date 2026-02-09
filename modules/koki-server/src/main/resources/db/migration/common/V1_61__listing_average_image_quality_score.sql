-- Add average image quality score column to listing table
ALTER TABLE T_LISTING
    ADD COLUMN average_image_quality_score DOUBLE PRECISION;
