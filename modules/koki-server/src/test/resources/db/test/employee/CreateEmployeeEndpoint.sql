INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (111, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (121, 1, 'omam.mbiyick@gmail.com', '---', 'Ray Sponsible', 1),
           (155, 1, 'roger.milla@gmail.com', '---', 'Roger Milla', 1);

INSERT INTO T_EMPLOYEE(id, tenant_fk)
    VALUES (121, 1);
