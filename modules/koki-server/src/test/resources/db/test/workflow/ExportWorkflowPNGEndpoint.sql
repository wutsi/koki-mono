INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_WORKFLOW(id, tenant_fk, name, description, active)
    VALUES(100, 1, 'w1', 'description w1', false),
          (110, 1, 'empty', null, false),
          (200, 2, 'w2', 'description w2', true);

INSERT INTO T_ACTIVITY(id, active, workflow_fk, name, type, description, tags, requires_approval)
    VALUES (110, true, 100, 'START', 1, 'Start the process', 'a=p1\nb=p2', true),
           (111, true, 100, 'WORKING', 3, 'fill the taxes', null, false),
           (112, true, 100, 'SEND', 6, null, null, false),
           (113, true, 100, 'SUBMIT', 7, null, null, false),
           (114, true, 100, 'STOP', 2, null, null, false);

INSERT INTO T_ACTIVITY_PREDECESSOR(activity_fk, predecessor_fk)
    VALUES (111, 110),
           (112, 111),
           (113, 111),
           (114, 112),
           (114, 113);
