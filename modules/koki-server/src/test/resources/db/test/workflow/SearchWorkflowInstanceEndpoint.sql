INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

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

INSERT INTO T_ACTIVITY(id, workflow_fk, role_fk, name, type, description, tags, requires_approval, active)
    VALUES (100, 100, null, 'START',   1, 'Start the process', 'a=p1\nb=p2', true, true),
           (101, 100, 11,   'WORKING', 3, 'fill the taxes', null, false, true),
           (102, 100, 10,   'SEND',    4, null, null, false, true),
           (103, 100, 10,   'SUBMIT',  4, null, null, false, true),
           (104, 100, null, 'STOP',    2, null, null, false, true),

           (110, 110, null, 'START',   1, null, null, false, true),
           (111, 110, null, 'WORKING', 4, null, null, false, true),
           (112, 110, null, 'SEND',    4, null, null, false, true),
           (113, 110, null, 'SUBMIT',  4, null, null, false, false),
           (114, 110, null, 'STOP',    2, null, null, false, true)
    ;

INSERT INTO T_ACTIVITY_PREDECESSOR(activity_fk, predecessor_fk)
    VALUES (101, 100),
           (102, 101),
           (103, 101),
           (104, 102),
           (104, 103),

           (111, 110),
           (112, 111),
           (113, 111),
           (114, 112),
           (114, 113);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, 100,  2, now()),
           ('wi-100-02', 1, 100, null, 2, now()),
           ('wi-100-03', 1, 100, null, 2, now()),
           ('wi-100-04', 1, 100, null, 2, now()),
           ('wi-100-05', 1, 100, null, 1, now()),
           ('wi-100-06', 1, 100, null, 2, now()),
           ('wi-110-01', 1, 110, null, 2, now()),

           ('wi-200-01', 2, 200, null, 2, now());

INSERT INTO T_WI_ACTIVITY(id, instance_fk, activity_fk, assignee_fk, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      'wi-100-01', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-100-02-start-done',      'wi-100-02', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-02-working-done',    'wi-100-02', 101, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-100-03-start-done',      'wi-100-03', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-03-working-running', 'wi-100-03', 101, null, null, 2, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-100-05-start-running',   'wi-100-05', 100, null, null, 2, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-100-06-start-done',      'wi-100-06', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-06-working-done',    'wi-100-06', 101, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-06-send-done',       'wi-100-06', 102, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-06-submit-working',  'wi-100-06', 103, null, null, 2, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-110-01-start-done',      'wi-110-01', 110, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-110-01-working-done',    'wi-110-01', 111, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30');

INSERT INTO T_WI_PARTICIPANT(instance_fk, user_fk, role_fk)
    VALUES ('wi-100-01', 100, 10),
           ('wi-100-01', 101, 11),
           ('wi-100-02', 100, 10),
           ('wi-100-02', 101, 11),
           ('wi-100-03', 100, 10),
           ('wi-100-03', 101, 11);
