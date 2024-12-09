INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active)
    VALUES(100, 1, 'w1', 'description w1', false),
          (110, 1, 'empty', null, false),
          (200, 2, 'w2', 'description w2', true);

INSERT INTO T_ACTIVITY(id, tenant_fk, active, workflow_fk, name, type, description, requires_approval)
    VALUES (110, 1, true, 100, 'START', 1, 'Start the process', true),
           (111, 1, true, 100, 'WORKING', 3, 'fill the taxes', false),
           (112, 1, true, 100, 'SEND', 6, null, false),
           (113, 1, true, 100, 'SUBMIT', 7, null, false),
           (114, 1, true, 100, 'STOP', 2, null, false);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, null),
           (100, 111, 112, null),
           (100, 111, 113, 'submit=true'),
           (100, 112, 114, null),
           (100, 113, 114, null);
