INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number)
    VALUES (111, 1, 3, 1, 2, 1000000);

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, first_name, last_name, email, phone_number, status, message, visit_requested_at)
    VALUES (100, 1, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 2, 'Hello world', '2026-12-30'),
           (200, 2, 222, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world', '2026-12-30');
