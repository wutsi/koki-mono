
INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1)
