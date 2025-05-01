INSERT INTO T_TYPE(id, tenant_fk, object_type, name, active)
    VALUES (100, 1, 2, 'primary', true),
           (101, 1, 2, 'secondary', true),
           (200, 2, 2, 'aa', false);

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email)
    VALUES(1000, 1,  'Inc', 'ray1@gmail.com'),
          (2000, 2, 'Inc', 'ray2@gmail.com');

INSERT INTO T_CONTACT(id, tenant_fk, contact_type_fk, account_fk, first_name, last_name, deleted)
    VALUES(100, 1, 100,  1000, 'Ray', 'Sponsible', false),
          (101, 1, null, null, 'X', 'Raymond', false),
          (102, 1, 100,  null, 'X', 'Y', false),
          (103, 1, 101,  1000, 'X', 'Y', false),
          (199, 1, null, null, 'X', 'Y', true),
          (200, 2, 100,  null, 'X', 'Y', false);
