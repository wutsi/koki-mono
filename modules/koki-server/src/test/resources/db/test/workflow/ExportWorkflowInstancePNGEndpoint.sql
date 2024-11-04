INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES(100, 1, 'w100', null, true, 'PARAM_1, PARAM_2');
;

INSERT INTO T_ACTIVITY(id, workflow_fk, role_fk, name, type, active)
    VALUES (100, 100, null, 'START',   1, true),
           (101, 100, 11,   'WORKING', 3, true),
           (102, 100, 10,   'SEND',    4, true),
           (103, 100, 10,   'SUBMIT',  4, true),
           (104, 100, null, 'STOP',    2, true);

INSERT INTO T_ACTIVITY_PREDECESSOR(activity_fk, predecessor_fk)
    VALUES (101, 100),
           (102, 101),
           (103, 101),
           (104, 102),
           (104, 103);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, null, 2, now());

INSERT INTO T_WI_ACTIVITY(id, instance_fk, activity_fk, assignee_fk, approval, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      'wi-100-01', 100, null, 0, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working-running', 'wi-100-01', 101, null, 0, null, 2, '2020-01-10 12:30', null);
