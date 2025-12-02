INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number)
    VALUES (111, 1, 3, 1, 2, 1000000);

INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name)
    VALUES (11, 1, 'thomas.nkono', 'thomas.nkono@gmail.com', '---', 'Thomas Nkono'),
           (12, 1, 'roger.milla', 'roger.milla@gmail.com', '---', 'Thomas Nkono');

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, user_fk, status, first_name, last_name, email, phone_number)
    VALUES (101, 1, 111, 12, 2, 'R', 'M', 'roger.milla@gmail.com', '--');
