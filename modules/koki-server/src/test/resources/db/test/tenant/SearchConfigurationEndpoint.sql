
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'a',   'a1'),
           (1, 'b',   'b1'),
           (1, 'c',   'c1'),
           (1, 'c.d', 'cd1'),

           (2, 'aa',  'aa1');
