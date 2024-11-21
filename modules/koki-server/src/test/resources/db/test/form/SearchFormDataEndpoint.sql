
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content, created_at)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}', '2020-01-01'),
           (110, 1, 'f-110', 'Form 110', true, '{}', '2020-01-02'),
           (120, 1, 'f-120', 'Form 120', false, '{}', '2020-01-03'),
           (130, 1, 'f-130', 'Form 130', true, '{}', '2020-01-04'),
           (200, 2, 'f-200', 'Form 200', true, '{}', now());

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, status, workflow_instance_id, data)
    VALUES (10011, 1, 100, 2, null,     '{}'),
           (10012, 1, 100, 1, null,     '{}'),
           (10013, 1, 100, 2, 'wi-100', '{}'),
           (10014, 1, 100, 1, 'wi-101', '{}'),
           (10015, 1, 100, 1, 'wi-102', '{}'),
           (11012, 1, 110, 2, null,     '{}'),
           (11013, 1, 110, 2, 'wi-103', '{}'),

           (20022, 2, 200, 1, 'wi-200', '{}');
