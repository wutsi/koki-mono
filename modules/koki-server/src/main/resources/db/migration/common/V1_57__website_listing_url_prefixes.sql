-- Add commercial property fields to listing table
ALTER TABLE T_LISTING ADD COLUMN units INT;
ALTER TABLE T_LISTING ADD COLUMN revenue BIGINT;

