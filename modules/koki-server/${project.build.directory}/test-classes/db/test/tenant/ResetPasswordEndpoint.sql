INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'US', 'USD', 'https://tenant-2.com', 'https://client.tenant-2.com');

INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name, status, photo_url)
    VALUES (11, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1, 'https://img.com/1.png'),
           (12, 1, 'john.smith', 'john.smith@gmail.com', '---', 'John Smith', 1, 'https://img.com/2.png'),
           (22, 2, 'roger.milla', 'roger.milla@gmail.com', '---', 'Roger Milla', 1, null);

INSERT INTO T_PASSWORD_RESET_TOKEN(id, tenant_fk, user_fk, created_at, expires_at)
    VALUES ('token-11', 1, 11, now(), date_add(now(), interval 1 day)),
           ('token-expired', 1, 12, date_sub(now(), interval 2 day), date_sub(now(), interval 1 day));
