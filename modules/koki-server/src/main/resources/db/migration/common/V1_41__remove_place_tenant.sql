-- Remove tenant association from places
-- Drop tenant index
DROP INDEX tenant ON T_PLACE;

-- Drop tenant_fk column
ALTER TABLE T_PLACE DROP COLUMN tenant_fk;

