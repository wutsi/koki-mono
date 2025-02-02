INSERT INTO T_TYPE(id, tenant_fk, object_type, name, active)
    VALUES (100, 1, 2, 'primary', true),
           (200, 2, 2, 'aa', false);

INSERT INTO T_ACCOUNT(id, tenant_fk, name)
    VALUES(1000, 1, 'Inc'),
          (2000, 2, 'Inc');
