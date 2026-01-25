INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url, qr_code_icon_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com', 'https://picsum.photos/150/150');

INSERT INTO T_AGENT(id, tenant_fk, user_fk)
    VALUES(100, 1, 11);
