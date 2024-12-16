INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, title, description, active, parameters, approver_role_fk)
    VALUES (100, 1, 'w100','workflow #100', 'Yo', true, 'PARAM_1, PARAM_2', 10);

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, title, type, active, event, input)
    VALUES (100, 1, 100, 'START',   'Start',   1, true,  null, null),
           (101, 1, 100, 'WORKING', 'Work...', 4, true,  'order-received', '{"payment_id": "transaction_id"}'),
           (102, 1, 100, 'WAITING', 'old...',  4, true,  'waiting', null),
           (103, 1, 100, 'STOP',    'Done',    2, true,  null, null);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 100, 101, null),
           (100, 100, 102, null),
           (100, 101, 103, null),
           (100, 102, 103, null);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100-01', 1, 100, 100,  2, now()),
           ('wi-100-02', 1, 100, 100,  2, now()),
           ('wi-100-03', 1, 100, 100,  2, now());

INSERT INTO T_WI_ACTIVITY(id, tenant_fk, workflow_instance_fk, activity_fk, status)
    VALUES ('wi-100-01-start-done',      1, 'wi-100-01', 100, 3),
           ('wi-100-01-working-running', 1, 'wi-100-01', 101, 2),
           ('wi-100-01-waiting-running', 1, 'wi-100-01', 102, 2),

           ('wi-100-02-start-done',      1, 'wi-100-02', 100, 3),
           ('wi-100-02-working-running', 1, 'wi-100-02', 101, 2),
           ('wi-100-02-waiting-running', 1, 'wi-100-02', 102, 2),

           ('wi-100-03-start-done',      1, 'wi-100-03', 100, 3);
