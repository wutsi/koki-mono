INSERT INTO T_FORM(id, tenant_fk, name, title, active, content, created_at)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"title":"Sample Form","description":"Description of the form"}', '2020-01-01'),
           (200, 2, 'f-200', 'Form 200', true, '{}', now());

INSERT INTO T_FORM_SUBMISSION(id, tenant_fk, form_fk, submitted_by_fk, workflow_instance_id, activity_instance_id, data)
    VALUES (10011, 1, 100, 11,  'wi-100', 'wi-100-01', '{"title":"Sample Form"}'),
           (20011, 2, 100, null, null,     null,        '{}');
