INSERT INTO T_FORM(id, tenant_fk, name, deleted, active, description)
    VALUES (100, 1, 'f-100', false, false, 'This is the F-100 form'),
           (110, 1, 'f-110', false, false, null),
           (199, 1, 'f-199', true, true, null),
           (200, 2, 'f-200', false, true, null);

