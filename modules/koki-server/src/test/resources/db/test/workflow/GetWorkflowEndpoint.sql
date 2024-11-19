INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'accountant'),
           (11, 1, 'technician'),
           (12, 1, 'boss');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active, parameters, approver_role_fk, workflow_instance_count)
    VALUES(100, 1, 'w1', 'description w1', false, 'PARAM_1, PARAM_2, PARAM_3', 10, 11),
          (200, 2, 'w2', 'description w2', true, null, null, 0),
          (300, 1, 'w3', 'activity with malformed tag', false, 'CLIENT_ID', null, 0);
;

INSERT INTO T_ACTIVITY(id, workflow_fk, role_fk, name, type, description, tags, requires_approval)
    VALUES (110, 100, null, 'START', 1, 'Start the process', 'a=p1\nb=p2', true),
           (111, 100, 11, 'WORKING', 3, 'fill the taxes', null, false),
           (112, 100, 10, 'SEND', 6, null, null, false),
           (113, 100, 10, 'SUBMIT', 7, null, null, false),
           (114, 100, null, 'STOP', 2, null, null, false),

           (310, 300, null, 'START', 1, 'malformed tag', 'a', true);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, null),
           (100, 111, 112, null),
           (100, 111, 113, 'submit=true'),
           (100, 112, 114, null),
           (100, 113, 114, null);
