INSERT INTO T_ATTRIBUTE(tenant_fk, name, label, description, choices, type, active)
    VALUES (1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (1, 'b', null, null, null, 2, true),
           (1, 'c', null, null, '', 3, false),

           (2, 'aa', null, null, '', 3, false),
           (2, 'bb', null, null, '', 3, false)
;
