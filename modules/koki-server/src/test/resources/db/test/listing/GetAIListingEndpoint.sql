INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type)
    VALUES (100, 1, 3, 1, 2),
           (110, 1, 3, 1, 2),
           (200, 2, 3, 1, 2);

INSERT INTO T_AI_LISTING(id, listing_fk, tenant_fk, text, result)
    VALUES (111, 100, 1, 'This is text', '{"foo":"bar"}'),
           (200, 200, 2, 'This is text', '{"foo":"bar"}')
