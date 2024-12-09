
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code)
    VALUES (100, 1, 'S-100', 'Sample script', 1, 'console.log(10+10)'),
           (200, 2, 'S-200', 'Script #200', 2, 'print(10+10)');
