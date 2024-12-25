INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, deleted)
    VALUES (100, 1, 'M-100', 'Subject', 'Hello', false, false),
           (110, 1, 'M-110', 'Subject', 'Hello', false, false),
           (199, 1, 'M-199', 'Subject', 'Hello', false, true),
           (200, 2, 'M-200', 'Subject', 'Hello', true, false);

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1');
;

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, message_fk)
    VALUES (110, 1, 100, 'START', 110);
