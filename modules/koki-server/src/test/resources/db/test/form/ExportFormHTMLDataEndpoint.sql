
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (22, 2, 'roger.milla@gmail.com', '---', 'Roger Milla', 1);

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"name":"FRM-001", "title":"Sample Form","description":"Description of the form"}'),
           (110, 1, 'f-110', 'Form 110', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, user_fk, status, workflow_instance_id, activity_instance_id, data)
    VALUES (10011, 1, 100, 11, 2, 'wi-100', 'wi-100-11', '{"A":"aa","B":"bb"}'),
           (20022, 2, 200, 22, 1, 'wi-200', 'wi-200-22', '{"X":"xx"}');
