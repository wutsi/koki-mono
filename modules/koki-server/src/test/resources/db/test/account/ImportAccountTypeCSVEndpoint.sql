INSERT INTO T_ACCOUNT_TYPE(tenant_fk, name, title, active)
    VALUES (1, 'a', 'description-a', true),
           (1, 'b', null, true),
           (1, 'c', null, false),

           (2, 'aa', null, false),
           (2, 'bb', null, false)
