
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, deleted, content, created_at)
    VALUES (100, 1, 'f-100', 'Form 100', true,  false, '{"title":"Sample Form","description":"Description of the form"}', '2020-01-01'),
           (110, 1, 'f-110', 'Form 110', true,  false, '{}', '2020-01-02'),
           (120, 1, 'f-120', 'Form 120', false, false, '{}', '2020-01-03'),
           (130, 1, 'f-130', 'Form 130', true,  false, '{}', '2020-01-04'),
           (199, 1, 'f-199', 'Form 130', true,  true, '{}', '2020-01-04'),
           (200, 2, 'f-200', 'Form 200', true,  false, '{}', now());
