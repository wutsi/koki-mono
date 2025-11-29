
INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer'),
           (12, 1, 'reader'),
           (20, 2, 'accountant'),
           (21, 2, 'technician');

INSERT INTO T_USER(id, device_id, tenant_fk, username, email, password, display_name, status, language, employer, mobile, country, city_fk)
    VALUES (11, 'xxx', 1, 'ray.sponsible', 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 3, 'fr', 'KOKI', '+15147580000', 'ca', 111),
           (22, null, 2, 'roger.milla', 'roger.milla@gmail.com', '---', 'Roger Milla', 3, 'ru', null, null, null, null);

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (11, 10),
           (11, 11),
           (11, 12);
