
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, title, active, content, created_at)
    VALUES (100, 1, 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}', '2020-01-01'),
           (110, 1, 'Form 110', true, '{}', '2020-01-02'),
           (120, 1, 'Form 120', false, '{}', '2020-01-03'),
           (130, 1, 'Form 130', true, '{}', '2020-01-04'),
           (200, 2, 'Form 200', true, '{}', now());
