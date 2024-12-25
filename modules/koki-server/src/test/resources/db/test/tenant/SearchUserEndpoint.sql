INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer'),
           (12, 1, 'reader'),
           (20, 2, 'accountant'),
           (21, 2, 'technician');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (12, 1, 'john.smith@gmail.com', '---', 'John Smith', 1),
           (13, 1, 'raymond-rougeau@gmail.com', '---', 'Raymond Rougeau', 2),
           (14, 1, 'hulk@gmail.com', '---', 'Hulk Hogan', 3),
           (15, 1, 'pp@gmail.com', '---', 'Peter Pan', 1),
           (16, 1, 'spiderman@gmail.com', '---', 'Peter Parker', 1),
           (17, 1, 'pf1969@gmail.com', '---', 'Peter Fonda', 1),
           (18, 1, 'pf1970@gmail.com', '---', 'Henry Fonda', 1),

           (22, 2, 'roger.milla@gmail.com', '---', 'Roger Milla', 1),
           (23, 1, 'user.23@gmail.com', '---', 'User 23', 1)
;

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (11, 10),
           (11, 11),
           (12, 11),
           (22, 20);
