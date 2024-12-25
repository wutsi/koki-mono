INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'accountant'),
           (11, 1, 'technician'),
           (12, 1, 'boss');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{}');

INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, deleted)
    VALUES (100, 1, 'M-100', 'Subject', 'Hello', false, false);

INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted, active)
    VALUES (100, 1, 'S-100', 'Create',        1, 'console.log(10+10)', false, true);


INSERT INTO T_SERVICE(id, tenant_fk, name, title)
    VALUES (100, 1, 'SRV-100', 'Sample script');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters, approver_role_fk, workflow_instance_count)
    VALUES(100, 1, 'w1', 'description w1', false, 'PARAM_1, PARAM_2, PARAM_3', 10, 11),
          (200, 2, 'w2', 'description w2', true, null, null, 0),
          (300, 1, 'w3', 'activity with malformed tag', false, 'CLIENT_ID', null, 0);
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, description, input, output, requires_approval, message_fk, service_fk, method, path)
    VALUES (110, 1, 100, null, 'START', 1, 'Start the process', '{"a":"p1","b":"p2"}', '{"x":"y"}', true, null, null, null, null),
           (112, 1, 100, 10, 'SEND', 6, null, null, null, false, 100, null, null, null),
           (111, 1, 100, 11, 'WORKING', 3, 'fill the taxes', null, null, false, null, null, null, null),
           (113, 1, 100, 10, 'SUBMIT', 7, null, null, null, false, null, 100, 'POST', '/activities'),
           (114, 1, 100, null, 'STOP', 2, null, null, null, false, null, null, null, null),

           (310, 1, 300, null, 'START', 1, 'malformed tag', null, null, true, null, null, null, null);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, null),
           (100, 111, 112, null),
           (100, 111, 113, 'submit=true'),
           (100, 112, 114, null),
           (100, 113, 114, null);
