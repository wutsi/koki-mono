
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (22, 2, 'roger.milla@gmail.com', '---', 'Roger Milla', 1);

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');
