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
    VALUES(100, 1, 'w1', 'description w1', true, 'PARAM_1, PARAM_2'),
          (200, 1, 'w2', 'no approver, no role, no parameter', true, null),
          (300, 1, 'w3', 'inactive', false, null),
          (400, 1, 'w4', 'inactive-start', false, null);

INSERT INTO T_ACTIVITY(id, workflow_fk, name, type)
    VALUES (100, 100, 'START',   1),
           (101, 100, 'WORKING', 3),
           (102, 100, 'SEND',    6),
           (103, 100, 'SUBMIT',  7),
           (104, 100, 'STOP',    2);


INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, 100, 2, now()),
           ('wi-100-02', 1, 100, 100, 2, now()),
           ('wi-100-03', 1, 100, 100, 1, now());

INSERT INTO T_WI_ACTIVITY(id, instance_fk, activity_fk, assignee_fk, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      'wi-100-01', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working-done',    'wi-100-01', 101, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-send-done',       'wi-100-01', 102, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-submit-done',     'wi-100-01', 103, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-stop-done',       'wi-100-01', 104, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),

           ('wi-100-02-start-done',      'wi-100-02', 100, null, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-02-working-running', 'wi-100-02', 101, null, null, 2, '2020-01-10 12:30', '2020-01-11 12:30');
