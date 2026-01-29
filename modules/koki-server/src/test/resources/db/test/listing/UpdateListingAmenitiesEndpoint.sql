INSERT INTO T_CATEGORY(id, type, level, name, long_name, active)
    VALUES (1100, 1, 0, 'A',  'A', true),
           (1200, 1, 0, 'B',  'B', true),
           (1300, 1, 0, 'C',  'C', true),
           (1400, 1, 0, 'D',  'C', true);

INSERT INTO T_AMENITY(id, category_fk, name, active)
    VALUES (1101, 1100, 'AA1', true),
           (1102, 1100, 'AA2', true),
           (1103, 1100, 'AA3', false),
           (1201, 1200, 'BB1', false),
           (1202, 1200, 'BB2', true);

INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type)
    VALUES(100, 1, 1, 1, 2),
          (101, 1, 2, 1, 2);

INSERT INTO T_LISTING_AMENITY(listing_fk, amenity_fk)
    VALUES(101, 1101),
          (101, 1102);
