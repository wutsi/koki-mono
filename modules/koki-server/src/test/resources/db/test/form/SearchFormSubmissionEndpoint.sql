
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content, created_at)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}', '2020-01-01'),
           (110, 1, 'f-110', 'Form 110', false,'{}', '2020-01-01'),
           (200, 2, 'f-200', 'Form 200', true, '{}', now());

INSERT INTO T_FORM_SUBMISSION(id, tenant_fk, form_fk, submitted_by_fk, workflow_instance_id, activity_instance_id, data)
    VALUES (10011, 1, 100, 11,   'wi-100', 'wi-100-01', null),
           (10012, 1, 100, 12,   'wi-100', 'wi-100-02', null),
           (10013, 1, 100, 13,   'wi-100', 'wi-100-03', null),
           (10014, 1, 110, null, 'wi-110', 'wi-110-01', null),
           (20011, 2, 100, null, null,     null,        null);
