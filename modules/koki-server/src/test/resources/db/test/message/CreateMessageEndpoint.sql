
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body)
    VALUES (100, 1, 'M-100', 'Subject ', 'Hello'),
           (200, 2, 'M-200', 'Subject ', 'Hello');
