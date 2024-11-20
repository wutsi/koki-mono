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

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, requires_approval)
    VALUES (100, 1, 100, null, 'START',   1, false),
           (101, 1, 100, 11,   'WORKING', 3, false),
           (102, 1, 100, 10,   'SEND',    4, false),
           (103, 1, 100, 10,   'SUBMIT',  4, false),
           (104, 1, 100, null, 'STOP',    2, false);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at, state, parameters)
    VALUES ('wi-100-01', 1, 100, 101, 2, now(), '{"customer_name":"Ray Sponsible", "customer_email":"ray.sponsible@gmail.com"}', '{"order_id": "123456"}');

INSERT INTO T_WI_ACTIVITY(id, tenant_fk, workflow_instance_fk, activity_fk, assignee_fk, approval, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      1, 'wi-100-01', 100, null, 0, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working-running', 1, 'wi-100-01', 101, null, 0, null, 2, '2020-01-10 12:30', null);

INSERT INTO T_WI_PARTICIPANT(workflow_instance_fk, user_fk, role_fk)
    VALUES ('wi-100-01', 100, 10),
           ('wi-100-01', 101, 11);
