INSERT INTO T_FORM(id, tenant_fk, name, title, active, deleted, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, false, '{"title":"Sample Form","description":"Description of the form"}'),
           (200, 2, 'f-200', 'Form 200', true, false, '{}');

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, status, workflow_instance_id, data)
    VALUES (10011, 1, 100, 2, 'wi-100', '{"A":"aa","B":"bb"}'),
           (20022, 2, 200, 1, 'wi-200', '{"X":"xx"}');
