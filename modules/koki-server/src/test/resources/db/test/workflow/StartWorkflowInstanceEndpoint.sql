INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'accountant'),
           (11, 1, 'technician'),
           (12, 1, 'boss');

INSERT INTO T_USER(id, tenant_fk, email, password, salt, display_name)
    VALUES (100, 1, 'ray.sponsible100@gmail.com', '--', '--', 'Ray Sponsible100'),
           (101, 1, 'ray.sponsible101@gmail.com', '--', '--', 'Ray Sponsible102'),
           (102, 1, 'ray.sponsible102@gmail.com', '--', '--', 'Ray Sponsible103'),
           (103, 1, 'ray.sponsible103@gmail.com', '--', '--', 'Ray Sponsible104'),

           (200, 2, 'ray.sponsible200@gmail.com', '--', '--', 'Ray Sponsible200');

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (100, 10),
           (101, 11),
           (102, 12);

INSERT INTO T_FORM(id, tenant_fk, name, title, active, deleted, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, false, '{}'),
           (110, 1, 'f-110', 'Form 110', true, false, '{}'),
           (120, 1, 'f-120', 'Form 120', true, false, '{}');

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, status, workflow_instance_id, data)
    VALUES (10011, 1, 100, 2, null, '{"A":"aa","B":"bb"}'),
           (11011, 1, 110, 2, null, '{"A":"aa","B":"bb"}'),
           (12011, 1, 120, 2, null, '{"A":"aa","B":"bb"}');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES(100, 1, 'w1', 'description w1', true, 'PARAM_1, PARAM_2'),
          (200, 1, 'w2', 'no approver, no role, no parameter', true, null),
          (300, 1, 'w3', 'inactive', false, null),
          (400, 1, 'w4', 'inactive-start', false, null),
          (500, 1, 'w5', 'linked_with_form', true, null),
          (600, 1, 'w6', 'linked_with_form_but_inactive', false, null),
          (700, 1, 'w7', 'linked_with_form_with_inactive_start', true, null)
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, form_fk, name, type, description, requires_approval, active)
    VALUES (110, 1, 100, null, null, 'START', 1, 'Start the process', false, true),
           (111, 1, 100, 11,   null, 'WORKING', 3, 'fill the taxes', false, true),
           (112, 1, 100, 10,   null, 'SEND', 6, null, false, true),
           (113, 1, 100, 10,   null, 'SUBMIT', 7, null, false, true),
           (114, 1, 100, null, null, 'STOP', 2, null, false, true),

           (210, 1, 200, null, null, 'START', 1, null, false, true),
           (211, 1, 200, null, null, 'WORKING', 3, null, false, true),
           (212, 1, 200, null, null, 'END', 2, null, false, true),

           (310, 1, 300, null, null, 'START', 1, null, true, true),

           (400, 1, 400, null, null, 'START', 1, null, true, false),

           (510, 1, 500, null, 100,  'START', 1, 'Start the process', false, true),
           (511, 1, 500, 11,   null, 'WORKING', 3, 'fill the taxes', false, true),
           (512, 1, 500, null, null, 'SEND', 6, null, false, true),
           (513, 1, 500, null, null, 'SUBMIT', 7, null, false, true),
           (514, 1, 500, null, null, 'STOP', 2, null, false, true),

           (610, 1, 600, null, 110,  'START', 1, 'Start the process', false, true),
           (611, 1, 600, 11,   null, 'WORKING', 3, 'fill the taxes', false, true),
           (612, 1, 600, null, null, 'STOP', 2, null, false, true),

           (710, 1, 700, null, 120,  'START', 1, 'Start the process', false, false),
           (711, 1, 700, 11,   null, 'WORKING', 3, 'fill the taxes', false, true),
           (712, 1, 700, null, null, 'STOP', 2, null, false, true);
;

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, null),
           (100, 111, 112, null),
           (100, 111, 113, 'submit=true'),
           (100, 112, 114, null),
           (100, 113, 114, null),

           (200, 210, 211, null),
           (200, 211, 212, null),

           (500, 510, 511, null),
           (500, 511, 512, null),
           (500, 511, 513, null),
           (500, 512, 514, null),
           (500, 513, 514, null)
;

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100', 1, 100, 100, 1, now()),
           ('wi-running', 1, 100, 100, 2, now()),
           ('wi-done', 1, 100, 100, 3, now()),
           ('wi-inactive-start', 1, 400, 100, 1, now());
