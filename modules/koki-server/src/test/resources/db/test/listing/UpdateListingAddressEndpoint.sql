INSERT INTO T_LOCATION(id, parent_fk, name, ascii_name, country)
    VALUES(222, 200, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number)
    VALUES(100, 1, 1, 1, 2, 2400001),
          (101, 1, 1, 1, 2, 2400002);
