INSERT INTO T_USER(id, tenant_fk, type, username, email, password, display_name, status, salt)
    VALUES (11, 1, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Ray Sponsible', 1, '...143.,..'),
           (12, 1, 1, 'not-active', 'not-active@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Not Active', 2, '...143.,..'),
           (13, 1, 2, 'woo.llc', 'woo.llc@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Active', 1, '...143.,..'),
           (55, 1, 1, 'client', 'client@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'John Smith', 1, '...143.,..'),
           (22, 2, 1, 'roger.milla','roger.milla@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Roger Milla', 1, '...143.,..');


INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(100, 1, 'WOO LLC', 'ray1@gmail.com', false);

UPDATE T_USER set account_fk=100 where id=13;

