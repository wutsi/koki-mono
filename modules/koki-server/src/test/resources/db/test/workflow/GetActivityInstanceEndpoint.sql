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

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES(100, 1, 'w100', null, true, 'PARAM_1, PARAM_2'),
          (110, 1, 'w110', null, true, null),
          (200, 2, 'w200', null, true, null);
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, description, input, requires_approval, active)
    VALUES (100, 1, 100, null, 'START',   1, 'Start the process', '{"a":"p1","b":"p2"}', true, true),
           (101, 1, 100, 11,   'WORKING', 3, 'fill the taxes', null, false, true),
           (102, 1, 100, 10,   'SEND',    4, null, null, false, true),
           (103, 1, 100, 10,   'SUBMIT',  4, null, null, false, true),
           (104, 1, 100, null, 'STOP',    2, null, null, false, true),

           (200, 2, 200, null, 'START',   1, null, null, false, true),
           (201, 2, 200, null, 'WORKING', 4, null, null, false, true),
           (202, 2, 200, null, 'SEND',    4, null, null, false, true),
           (203, 2, 200, null, 'SUBMIT',  4, null, null, false, false),
           (204, 2, 200, null, 'STOP',    2, null, null, false, true)
    ;

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, 100,  2, now()),
           ('wi-200-01', 2, 200, null, 2, now());

INSERT INTO T_WI_ACTIVITY(id, tenant_fk, workflow_instance_fk, activity_fk, assignee_fk, approver_fk, status, approval, started_at, done_at)
    VALUES ('wi-100-01-start-done',      1, 'wi-100-01', 100, 100, 101, 3, 1, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-200-01-start-done',      2, 'wi-200-01', 200, null, null, 3, 0, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-200-01-working-working', 2, 'wi-200-01', 201, 101,  102,  2, 1, '2020-01-10 12:30', '2020-01-11 12:30');
