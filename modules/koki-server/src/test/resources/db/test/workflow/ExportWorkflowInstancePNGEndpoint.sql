INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES(100, 1, 'w100', null, true, 'PARAM_1, PARAM_2');
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, type, active)
    VALUES (100, 1, 100, 'START',   1, true),
           (101, 1, 100, 'WORKING', 3, true),
           (102, 1, 100, 'SEND',    4, true),
           (103, 1, 100, 'SUBMIT',  4, true),
           (104, 1, 100, 'STOP',    2, true);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 100, 101, null),
           (100, 101, 102, null),
           (100, 101, 103, 'submit=true'),
           (100, 102, 104, null),
           (100, 103, 104, null);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, null, 2, now());

INSERT INTO T_WI_ACTIVITY(id, tenant_fk, workflow_instance_fk, activity_fk, assignee_fk, approval, approver_fk, status, started_at, done_at)
    VALUES ('wi-100-01-start-done',      1, 'wi-100-01', 100, null, 0, null, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working-running', 1, 'wi-100-01', 101, null, 0, null, 2, '2020-01-10 12:30', null);
