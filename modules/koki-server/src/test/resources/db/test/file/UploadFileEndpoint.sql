INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.test.com');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'storage.type', 'LOCAL');
