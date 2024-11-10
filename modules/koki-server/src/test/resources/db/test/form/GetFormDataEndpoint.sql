
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, data)
    VALUES (100, 1, 100, '{"var1":"value1", "var2":"value2"}'),
           (200, 2, 200, '{"foo":"bar"}');
