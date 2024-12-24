INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, deleted)
    VALUES (100, 1, 'M-100', 'Subject', 'Hello', false, false),
           (200, 2, 'M-200', 'Subject', 'Hello', true, false);

INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted, active)
    VALUES (100, 1, 'S-100', 'Create',        1, 'console.log(10+10)', false, true);

INSERT INTO T_SERVICE(id, tenant_fk, name, title)
    VALUES (100, 1, 'SRV-100', 'Sample script');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, title, description, active, parameters, approver_role_fk)
    VALUES (100, 1, 'w100','workflow #100', 'Yo', true, 'PARAM_1, PARAM_2', 10),
           (200, 2, 'w200','workflow #200', null, true, null, null);

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, title, type, active, form_fk, message_fk, script_fk, event, role_fk, requires_approval, description, service_fk, path, method, recipient_email, recipient_display_name)
    VALUES (100, 1, 100, 'START',   'Start',   1, true,  null, null, null, null, null, false, 'Starting the process', null, null, null, null, null),
           (101, 1, 100, 'WORKING', 'Work...', 3, true,  100,  100,  100,  'order-received', 10,   true, 'Performing the task', 100, '/activities', 'POST', 'ray.sponsible@gmail.com', 'Ray Sponsible'),
           (102, 1, 100, 'OLD',     'old...',  3, false, null, null, null, null, 10,   true, null, null, null, null, null, null),
           (103, 1, 100, 'STOP',    'Done',    2, true,  null, null, null, null, null, false, null, null, null, null, '', '');

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 100, 101, null),
           (100, 100, 102, 'submit=false'),
           (100, 101, 103, 'submit=true'),
           (100, 102, 103, null);
