INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, seller_agent_user_fk)
    VALUES (111, 1, 3, 1, 2, 1111);

INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name)
    VALUES (11, 1, 'thomas.nkono', 'thomas.nkono@gmail.com', '---', 'Thomas Nkono'),
           (12, 1, 'roger.milla', 'roger.milla@gmail.com', '---', 'Thomas Nkono'),
           (13, 1, 'omam.mbiyick', 'omam.mbiyick@gmail.com', '---', 'Omam Mbiyick');

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, agent_user_fk,  user_fk, status, created_at, modified_at)
    VALUES (101, 1, 111, 1111, 12, 2, '2024-01-10 10:00:00', '2024-01-10 10:00:00'),
           (102, 1, null, 7777, 12, 2, '2024-01-10 10:00:00', '2024-01-10 10:00:00');
