
INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, deleted)
    VALUES (100, 1, 'M-100', 'Subject', 'Hello', false, false),
           (199, 1, 'M-199', 'Subject', 'Hello', false, true),
           (200, 2, 'M-200', 'Subject', 'Hello', true, false);
