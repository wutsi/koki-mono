INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-2.com', 'https://client.tenant-2.com');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'delete', 'a1'),
           (1, 'update', 'b1'),
           (1, 'batch-0', 'batch-140'),
           (1, 'batch-1', 'batch-141'),
           (1, 'batch-2', 'batch-142'),

           (2, 'other-tenant', 'aa1');
