INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_ROLE(id, tenant_fk, name, description, active)
    VALUES (10, 1, 'a', 'description-a',  true),
           (11, 1, 'b', null, true),
           (12, 1, 'c', null, false),

           (20, 2, 'aa', null, false),
           (22, 2, 'bb', null, false)
;
