INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (12, 1, 'john.smith', 'john.smith@gmail.com', '---', 'John Smith', 1);
