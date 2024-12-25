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

INSERT INTO T_WORKFLOW(id, tenant_fk, name, title, description, active, parameters, workflow_instance_count, approver_role_fk)
    VALUES(100, 1, 'w100', 'Workflow 100',  'This is the description', true, 'PARAM_1, PARAM_2', 0, null),
          (110, 1, 'w110', 'Workflow 110',   null, true, null,  10, 11),
          (120, 1, 'w120', 'Workflow 120',   null, false, null, 4,  null),
          (130, 1, 'w130', 'ZWorkflow 130',  null, false, null, 0,  11),
          (200, 2, 'w200', 'Workflow 200',   null, true, null,  0,  null);

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name)
    VALUES (100, 1, 100, null, 'START'),
           (102, 1, 100, 10,   'SEND'),
           (101, 1, 100, 11,   'WORKING'),
           (103, 1, 100, 10,   'SUBMIT'),
           (104, 1, 100, null, 'STOP'),

           (110, 1, 110, null, 'START'),
           (112, 1, 110, 10,   'SEND'),
           (111, 1, 110, 10,   'WORKING'),
           (113, 1, 110, 10,   'SUBMIT'),
           (114, 1, 110, null, 'STOP'),

           (120, 1, 120, null, 'START'),
           (122, 1, 120, 12,   'SEND'),
           (121, 1, 120, null, 'WORKING'),
           (123, 1, 120, 12,   'SUBMIT'),
           (124, 1, 120, null, 'STOP');

