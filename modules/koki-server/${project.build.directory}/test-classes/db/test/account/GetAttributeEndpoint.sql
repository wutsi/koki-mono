INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active, required)
    VALUES (100, 1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true, true),
           (200, 2, 'bb', null, null, '', 3, false, false)
;
