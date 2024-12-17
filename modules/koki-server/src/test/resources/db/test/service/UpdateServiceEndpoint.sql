
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_SERVICE(id, tenant_fk, name, title, deleted)
    VALUES (100, 1, 'SRV-100', 'Sample script', false),
           (110, 1, 'SRV-110', 'Sample script', false),
           (120, 1, 'SRV-120', 'Sample script', false),
           (199, 1, 'SRV-199', 'Sample script', true),
           (200, 2, 'SRV-200', 'Sample script', false);
