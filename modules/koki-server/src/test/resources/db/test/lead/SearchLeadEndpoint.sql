INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number, seller_agent_user_fk)
    VALUES (111, 1, 3, 1, 2, 1000000, null),
           (222, 1, 3, 1, 2, 2000000, null),
           (333, 1, 3, 1, 2, 3000000, 3);

INSERT INTO T_LEAD(id, tenant_fk, user_fk, listing_fk, first_name, last_name, email, phone_number, status, message)
    VALUES (100, 1, null, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world'),
           (101, 1, null, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world'),
           (200, 1, null, 222, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world'),
           (201, 1, null, 222, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world'),
           (300, 1, null, 333, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world'),
           (400, 1, null, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 2, 'Hello world'),
           (401, 1, null, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 3, 'Hello world'),
           (402, 1, null, 111, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 3, 'Hello world'),
           (500, 1, 1111, 555, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1, 'Hello world');
