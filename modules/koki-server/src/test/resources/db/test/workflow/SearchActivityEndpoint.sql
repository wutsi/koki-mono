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

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, requires_approval, active)
    VALUES (100, 1, 100, null, 'START',   1, false, true),
           (101, 1, 100, 11,   'WORKING', 3, false, true),
           (102, 1, 100, 10,   'SEND',    4, false, true),
           (103, 1, 100, 10,   'SUBMIT',  4, false, true),
           (104, 1, 100, null, 'STOP',    2, false, true),

           (110, 1, 110, null, 'START',   1, false, true),
           (111, 1, 110, null, 'WORKING', 4, true,  true),
           (112, 1, 110, null, 'SEND',    4, false, true),
           (113, 1, 110, null, 'SUBMIT',  4, false, true),
           (114, 1, 110, null, 'STOP',    2, false, true),
           (115, 1, 110, null, 'STOP2',   2, false, false),

           (200, 2, 200, null, 'START',   1, false, true),
           (201, 2, 200, null, 'WORKING', 4, true,  true),
           (202, 2, 200, null, 'SEND',    4, false, true),
           (203, 2, 200, null, 'SUBMIT',  4, false, true),
           (204, 2, 200, null, 'STOP',    2, false, true)
    ;
