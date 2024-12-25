INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'a',   'a1'),
           (1, 'b',   'b1'),
           (1, 'c',   'c1'),
           (1, 'c.d', 'cd1'),

           (2, 'aa',  'aa1');
