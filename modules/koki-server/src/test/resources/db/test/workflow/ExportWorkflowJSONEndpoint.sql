INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES (100, 1, 'w100', null, true, 'PARAM_1, PARAM_2'),
           (200, 2, 'w200', null, true, null);
;

INSERT INTO T_ACTIVITY(id, workflow_fk, name, title, type, active, form_fk, role_fk, requires_approval)
    VALUES (100, 100, 'START',   'Start',   1, true,  null, null, false),
           (101, 100, 'WORKING', 'Work...', 3, true,  100,  10,   true),
           (102, 100, 'OLD',     'old...',  3, false, null, 10,   true),
           (103, 100, 'STOP',    'Done',    2, true,  null, null, false);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 100, 101, null),
           (100, 100, 102, 'submit=false'),
           (100, 101, 103, 'submit=true'),
           (100, 102, 103, null);
