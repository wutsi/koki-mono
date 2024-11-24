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
          (120, 1, 'w120', null, true, null),
          (200, 2, 'w200', null, true, null);
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, role_fk, name, type, title, description, tags, requires_approval, active)
    VALUES (100, 1, 100, null, 'START',   1, 'Workflow 100', 'Start the process', 'a=p1\nb=p2', true, true),
           (101, 1, 100, 11,   'WORKING', 3, 'Workflow 101', 'fill the taxes', null, false, true),
           (102, 1, 100, 10,   'SEND',    4, 'Workflow 102', null, null, false, true),
           (103, 1, 100, 10,   'SUBMIT',  4, 'Workflow 103', null, null, false, true),
           (104, 1, 100, null, 'STOP',    2, 'Workflow 104', null, null, false, true),

           (110, 1, 110, null, 'START',   1, 'Workflow 100', null, null, false, true),
           (111, 1, 110, null, 'WORKING', 4, 'Workflow 111', null, null, false, true),
           (112, 1, 110, null, 'SEND',    4, 'Workflow 112', null, null, false, true),
           (113, 1, 110, null, 'SUBMIT',  4, 'Workflow 113', null, null, false, false),
           (114, 1, 110, null, 'STOP',    2, 'Workflow 114', null, null, false, true)
    ;

INSERT INTO T_WORKFLOW_INSTANCE(id, tenant_fk, workflow_fk, approver_fk, status, start_at, created_by_fk)
    VALUES ('wi-100-01', 1, 100, 100,  3, now(),        null),
           ('wi-100-02', 1, 100, null, 2, '2020-01-05', null),
           ('wi-100-03', 1, 100, null, 2, now(),        null),
           ('wi-100-04', 1, 100, null, 2, '2020-01-11', null),
           ('wi-100-05', 1, 100, null, 1, '2020-01-20', null),
           ('wi-100-06', 1, 100, null, 2, now(),        12),
           ('wi-110-01', 1, 110, null, 3, now(),        12),
           ('wi-120-01', 1, 120, null, 3, now(),        null),

           ('wi-200-01', 2, 200, null, 2, now(),        null);

INSERT INTO T_WI_PARTICIPANT(workflow_instance_fk, user_fk, role_fk)
    VALUES ('wi-100-01', 100, 10),
           ('wi-100-01', 101, 11),
           ('wi-100-02', 102, 12),
           ('wi-100-02', 100, 10),
           ('wi-100-02', 101, 11),
           ('wi-100-03', 102, 12);
