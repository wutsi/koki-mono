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
          (400, 1, 'w4', 'with instances', true, null);
;

INSERT INTO T_ACTIVITY(id, workflow_fk, role_fk, name, type, description, tags, requires_approval)
    VALUES (110, 100, null, 'START', 1, 'Start the process', 'a=p1\nb=p2', true),
           (111, 100, 11, 'WORKING', 3, 'fill the taxes', null, false),
           (112, 100, 10, 'SEND', 6, null, null, false),
           (113, 100, 10, 'SUBMIT', 7, null, null, false),
           (114, 100, null, 'STOP', 2, null, null, false),

           (210, 200, null, 'START', 1, null, null, false),
           (211, 200, null, 'WORKING', 3, null, null, false),
           (212, 200, null, 'END', 2, null, null, false),

           (310, 300, null, 'START', 1, null, null, true);

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at, state)
    VALUES ('wi-400-01', 1, 400, null, 2, now(), '{}'),
           ('wi-400-02', 1, 400, null, 2, now(), '{}'),
           ('wi-400-03', 1, 400, null, 1, now(), '{}'),
           ('wi-410-01', 1, 400, 100,  2, now(), '{"A":"oldA", "C":"cc"}'),
           ('wi-410-02', 1, 400, 100, 2, now(), '{}');
