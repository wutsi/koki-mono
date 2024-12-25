INSERT INTO T_FORM(id, tenant_fk, name, title, active, deleted, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, false, '{"title":"Sample Form","description":"Description of the form"}'),
           (110, 1, 'f-110', 'Form 110', true, false, '{}'),
           (199, 1, 'f-199', 'Form 199', true, true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, true, '{}');


INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1');
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, form_fk)
    VALUES (110, 1, 100, 'START', 110);
