
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, title, active, content)
    VALUES (100, 1, 'Form 100', true, '{}'),
           (200, 2, 'Form 200', true, '{}');
