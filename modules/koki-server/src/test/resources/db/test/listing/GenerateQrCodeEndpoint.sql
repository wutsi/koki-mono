INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url, qr_code_icon_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com', 'https://picsum.photos/150/150');

INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number)
    VALUES (100, 1, 3, 1, 2, 1000000),
           (200, 2, 3, 1, 2, 2000000);
