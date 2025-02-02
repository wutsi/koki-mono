INSERT INTO T_TYPE(id, tenant_fk, object_type, name, active)
    VALUES (100, 1, 2, 'primary', true),
           (200, 2, 2, 'aa', false);

INSERT INTO T_ACCOUNT(id, tenant_fk, name)
    VALUES(1000, 1,  'Inc'),
          (2000, 2, 'Inc');

INSERT INTO T_CONTACT(id, tenant_fk, first_name, last_name, deleted)
    VALUES(100, 1, 'X', 'Y', false),
          (199, 1, 'X', 'Y', true),
          (200, 2, 'X', 'Y', false);
