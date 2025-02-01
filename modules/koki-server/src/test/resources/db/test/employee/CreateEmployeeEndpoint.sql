INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status, type)
    VALUES (111, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1, 0),
           (121, 1, 'omam.mbiyick@gmail.com', '---', 'Ray Sponsible', 1, 0),
           (155, 1, 'roger.milla@gmail.com', '---', 'Roger Milla', 1, 2);

INSERT INTO T_EMPLOYEE(id, tenant_fk)
    VALUES (121, 1);
