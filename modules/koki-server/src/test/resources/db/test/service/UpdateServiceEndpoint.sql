
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_SERVICE(id, tenant_fk, name, title)
    VALUES (100, 1, 'SRV-100', 'Sample script');
