-- Add legal information fields to listing table
ALTER TABLE T_LISTING ADD COLUMN subdivided BOOLEAN DEFAULT NULL;
ALTER TABLE T_LISTING ADD COLUMN morcelable BOOLEAN DEFAULT NULL;

DROP INDEX idx_listing_legal ON T_LISTING;

