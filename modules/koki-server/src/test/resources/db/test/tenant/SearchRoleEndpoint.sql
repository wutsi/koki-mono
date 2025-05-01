INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-2.com', 'https://client.tenant-2.com');

INSERT INTO T_ROLE(id, tenant_fk, name, description, active, deleted)
    VALUES (10, 1, 'a',  'description-a',  true, false),
           (11, 1, 'b',  null, true, false),
           (12, 1, 'c',  null, false, false),
           (19, 1, '19', null, false, true),

           (20, 2, 'aa', null, false, false),
           (22, 2, 'bb', null, false, false)
;
