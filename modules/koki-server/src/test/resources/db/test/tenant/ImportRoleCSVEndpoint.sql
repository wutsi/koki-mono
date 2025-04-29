INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_ROLE(tenant_fk, name, description, active)
    VALUES (1, 'a', 'description-a', true),
           (1, 'b', null, true),
           (1, 'c', null, false),
           (1, 'x', null, true),

           (2, 'aa', null, false),
           (2, 'bb', null, false)
;
