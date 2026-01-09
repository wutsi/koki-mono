ALTER TABLE T_LISTING ADD COLUMN property_category INT;

UPDATE T_LISTING set property_category=1 WHERE property_type BETWEEN 1 AND 6;
UPDATE T_LISTING set property_category=3 WHERE property_type>6;
UPDATE T_LISTING set property_category=2 WHERE property_type=8;
