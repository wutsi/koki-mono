
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"name":"FRM-001", "title":"Sample Form","description":"Description of the form"}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters)
    VALUES(100, 1, 'w100', null, true, 'PARAM_1, PARAM_2');

INSERT INTO T_ACTIVITY(id, workflow_fk, name, type, requires_approval)
    VALUES (100, 100, 'START',   1, false),
           (101, 100, 'WORKING', 3, false),
           (102, 100, 'SEND',    4, false),
           (103, 100, 'SUBMIT',  4, false),
           (104, 100, 'STOP',    2, false);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, status, start_at, state, parameters)
    VALUES ('wi-100-01', 1, 100, 2, now(), '{"customer_name":"Ray Sponsible", "customer_email":"ray.sponsible@gmail.com"}', '{"order_id": "123456"}');

INSERT INTO T_WI_ACTIVITY(id, instance_fk, activity_fk, assignee_fk, approval, status, started_at, done_at)
    VALUES ('wi-100-01-start',   'wi-100-01', 100, null, 0, 3, '2020-01-10 12:30', '2020-01-11 12:30'),
           ('wi-100-01-working', 'wi-100-01', 101, null, 0, 2, '2020-01-10 12:30', null);
