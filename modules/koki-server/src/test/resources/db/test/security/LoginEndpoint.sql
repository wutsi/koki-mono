
INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status, salt)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Ray Sponsible', 1, '...143.,..'),
           (22, 2, 'roger.milla@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Roger Milla', 1, '...143.,..');
