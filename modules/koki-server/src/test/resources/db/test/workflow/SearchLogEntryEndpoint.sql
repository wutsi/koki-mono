INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_USER(id, tenant_fk, email, password, salt, display_name)
    VALUES (100, 1, 'ray.sponsible100@gmail.com', '--', '--', 'Ray Sponsible100'),
           (101, 1, 'ray.sponsible101@gmail.com', '--', '--', 'Ray Sponsible102'),
           (102, 1, 'ray.sponsible102@gmail.com', '--', '--', 'Ray Sponsible103'),
           (103, 1, 'ray.sponsible103@gmail.com', '--', '--', 'Ray Sponsible104'),

           (200, 2, 'ray.sponsible200@gmail.com', '--', '--', 'Ray Sponsible200');

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w100'),
          (110, 1, 'w110'),
          (200, 2, 'w200');
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type)
    VALUES (100, 1, 100, null, 'START',   1),
           (101, 1, 100, 11,   'WORKING', 3),
           (102, 1, 100, 10,   'SEND',    4),
           (103, 1, 100, 10,   'SUBMIT',  4),
           (104, 1, 100, null, 'STOP',    2);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 100, 101, null),
           (100, 101, 102, null),
           (100, 101, 103, 'submit=true'),
           (100, 102, 104, null),
           (100, 103, 104, null);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at, state)
    VALUES ('wi-100-01', 1, 100, null, 2, now(), '{}'),
           ('wi-100-02', 1, 100, null, 2, now(), '{}');


INSERT INTO T_WI_ACTIVITY(id, tenant_fk, workflow_instance_fk, activity_fk, assignee_fk, approval, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      1, 'wi-100-01', 100, null, 0, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working-running', 1, 'wi-100-01', 101, null, 0, null, 2, '2020-01-10 12:30', null),
           ('wi-100-02-start-done',      1, 'wi-100-02', 100, null, 0, null, 3, '2020-01-10 12:30', '2020-01-11 12:30')
;

INSERT INTO T_WI_LOG_ENTRY(id, tenant_fk, type, workflow_instance_fk, activity_instance_fk, message, created_at)
    VALUES ('100-01-001', 1, 1, 'wi-100-01', 'wi-100-01-start-done',      'Starting', '2020-01-01'),
           ('100-01-002', 1, 3, 'wi-100-01', 'wi-100-01-start-done',      'Filed',    '2020-01-02'),
           ('100-01-003', 1, 1, 'wi-100-01', 'wi-100-01-start-done',      'Done',     '2020-01-03'),

           ('101-01-001', 1, 1, 'wi-100-01', 'wi-100-01-working-running', 'Done',     '2020-01-03'),
           ('101-01-002', 1, 1, 'wi-100-01', 'wi-100-01-working-running', 'Yo..',     '2020-01-03'),

           ('101-02-001', 1, 1, 'wi-100-02', 'wi-100-02-start-done',      'Done',     '2020-01-03')
;

