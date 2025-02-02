INSERT INTO T_SERVICE(id, tenant_fk, name, title, deleted)
    VALUES (100, 1, 'SRV-100', 'Sample script', false),
           (110, 1, 'SRV-110', 'Sample script', false),
           (120, 1, 'SRV-120', 'Sample script', false),
           (199, 1, 'SRV-199', 'Sample script', true),
           (200, 2, 'SRV-200', 'Sample script', false);

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1');

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, service_fk)
    VALUES (110, 1, 100, 'START', 110);
