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
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, description, requires_approval, active)
    VALUES (110, 1, 100, null, 'START', 1, 'Start the process', false, true),
           (111, 1, 100, 11, 'WORKING', 3, 'fill the taxes', false, true),
           (112, 1, 100, 10, 'SEND', 6, null, false, true),
           (113, 1, 100, 10, 'SUBMIT', 7, null, false, true),
           (114, 1, 100, null, 'STOP', 2, null, false, true),

           (210, 1, 200, null, 'START', 1, null, false, true),
           (211, 1, 200, null, 'WORKING', 3, null, false, true),
           (212, 1, 200, null, 'END', 2, null, false, true),

           (310, 1, 300, null, 'START', 1, null, true, true),

           (400, 1, 400, null, 'START', 1, null, true, false);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, null),
           (100, 111, 112, null),
           (100, 111, 113, 'submit=true'),
           (100, 112, 114, null),
           (100, 113, 114, null),

           (200, 210, 211, null),
           (200, 211, 212, null);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at)
    VALUES ('wi-100', 1, 100, 100, 1, now()),
           ('wi-running', 1, 100, 100, 2, now()),
           ('wi-done', 1, 100, 100, 3, now()),
           ('wi-inactive-start', 1, 400, 100, 1, now());
