INSERT INTO T_TYPE(tenant_fk, object_type, name, description, active)
    VALUES (1, 1, 'a', 'description-a', true),
           (1, 1, 'b', null, true),
           (1, 1, 'c', null, false),
           (1, 1, 'x', null, true),

           (2, 1, 'aa', null, false),
           (2, 1, 'bb', null, false);
