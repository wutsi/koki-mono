INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'accountant'),
           (11, 1, 'technician'),
           (12, 1, 'boss');

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1'),
          (200, 2, 'w2');

INSERT INTO T_ACTIVITY(id, workflow_fk, role_fk, name, type)
    VALUES (110, 100, null, 'START', 1),
           (111, 100, 11, 'OLD', 4),
           (112, 100, null, 'INVOICE', 7),
           (113, 100, null, 'STOP', 2);

INSERT INTO T_FLOW(workflow_fk, from_fk, to_fk, expression)
    VALUES (100, 110, 111, 'client_status=false'),
           (100, 111, 112, null),
           (100, 112, 113, null);
