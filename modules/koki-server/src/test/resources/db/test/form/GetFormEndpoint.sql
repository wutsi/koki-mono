
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, deleted, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, false, '{"title":"Sample Form","description":"Description of the form"}'),
           (199, 1, 'f-199', 'Form 199', true, true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, true, '{}');
