INSERT INTO T_FORM(id, tenant_fk, name, deleted, active, description)
    VALUES (100, 1, 'f-100', false, false, 'This is the F-100 form'),
           (110, 1, 'f-110', false, false, null),
           (120, 1, 'f-120', false, true, null),
           (130, 1, 'f-120', false, true, null),
           (199, 1, 'f-199', true, true, null),
           (200, 2, 'f-200', false, true, null);


INSERT INTO T_FORM_OWNER(form_fk, owner_fk, owner_type)
    VALUES (100, 111, 2),
           (130, 111, 2),
           (199, 111, 2);
