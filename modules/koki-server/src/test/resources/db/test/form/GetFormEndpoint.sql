INSERT INTO T_FORM(id, tenant_fk, code, name, deleted, active, description)
    VALUES (100, 1, 'T-100', 'f-100', false, false, 'This is the F-100 form'),
           (110, 1, 'T-110', 'f-110', false, false, null),
           (199, 1, 'T-199', 'f-199', true, true, null),
           (200, 2, 'T-200', 'f-200', false, true, null);

