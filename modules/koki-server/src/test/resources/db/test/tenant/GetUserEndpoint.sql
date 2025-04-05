
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer'),
           (12, 1, 'reader'),
           (20, 2, 'accountant'),
           (21, 2, 'technician');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status, type, language)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1, 1, 'fr'),
           (22, 2, 'roger.milla@gmail.com', '---', 'Roger Milla', 1, 0, 'ru');

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (11, 10),
           (11, 11),
           (11, 12);
