INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1'),
          (200, 2, 'w2');

INSERT INTO T_ACTIVITY(id, workflow_fk, code, name, type)
    VALUES (110, 100, 'START', 'starting...', 1),
           (111, 100, 'OLD', 'working...', 4),
           (112, 100, 'STOP', 'end...', 2)
    ;

INSERT INTO T_ACTIVITY_PREDECESSOR(activity_fk, predecessor_fk)
    VALUES (111, 110),
           (112, 111);
