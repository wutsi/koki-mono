INSERT INTO T_TAX_TYPE(tenant_fk, name, description, active)
    VALUES (1, 'a', 'description-a', true),
           (1, 'b', null, true),
           (1, 'c', null, false),
           (1, 'x', null, false),

           (2, 'aa', null, false),
           (2, 'bb', null, false);
