INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number, seller_agent_user_fk)
    VALUES (111, 1, 3, 1, 2, 1000000, null),
           (222, 1, 3, 1, 2, 2000000, null),
           (333, 1, 3, 1, 2, 3000000, 3),
           (555, 1, 3, 1, 2, 30000555, null);

INSERT INTO T_LEAD(id, tenant_fk, user_fk, listing_fk, status, agent_user_fk)
    VALUES (100, 1, 1, 111, 1, 1),
           (101, 1, 2, 111, 1, 1),
           (200, 1, 3, 222, 1, 1),
           (201, 1, 4, 222, 1, 1),
           (300, 1, 5, 333, 1, 3),
           (301, 1, 5, null, 1, 3),
           (400, 1, 6, 111, 2, 1),
           (401, 1, 7, 111, 3, 1),
           (402, 1, 8, 111, 3, 1),
           (500, 1, 1111, 555, 1, 1),
           (600, 1, 2222, 555, 1, 1),
           (601, 1, 2222, 333, 1, 1),
           (602, 1, 2223, null, 1, 1)
;

INSERT INTO T_USER(id, tenant_fk, display_name, email, username, password)
    VALUES (1, 1, 'Yo Man', 'yo.man@gmail.com', 'yo.man', 'Hello world'),
           (2, 1, 'Ray Band', 'ray.band@gmail.com', 'ray.band', 'Hello world'),
           (2222, 1, 'Roger Milla', 'roger.milla@gmail.com', 'roger.milla', 'Hello world'),
           (2223, 1, 'Buck Roger', 'buck.roger@gmail.com', 'buck.roger', 'Hello world')
;
