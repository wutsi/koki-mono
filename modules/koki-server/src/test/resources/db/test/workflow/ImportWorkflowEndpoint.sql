INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'accountant'),
           (11, 1, 'technician'),
           (12, 1, 'boss');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, deleted)
    VALUES (100, 1, 'M-100', 'Subject', 'Hello', false, false),
           (200, 2, 'M-200', 'Subject', 'Hello', true, false);

INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted, active)
    VALUES (100, 1, 'S-100', 'Create',        1, 'console.log(10+10)', false, true);

INSERT INTO T_WORKFLOW(id, tenant_fk, name, workflow_instance_count)
    VALUES(100, 1, 'w1', 0),
          (110, 1, 'w-110', 0),
          (120, 1, 'w-120', 10),
          (200, 2, 'w2', 0);

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type)
    VALUES (110, 1, 100, null, 'START', 1),
           (111, 1, 100, 11, 'OLD', 4),
           (112, 1, 100, null, 'INVOICE', 7),
           (113, 1, 100, null, 'STOP', 2);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, 'client_status=false'),
           (100, 111, 112, null),
           (100, 112, 113, null);
