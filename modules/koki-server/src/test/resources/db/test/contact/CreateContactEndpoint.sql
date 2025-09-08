INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 3, 'Montreal', 'Montreal', 'CA'),
           (222, 777, 3, 'Quebec',   'Quebec', 'CA');

INSERT INTO T_TYPE(id, tenant_fk, object_type, name, active)
    VALUES (100, 1, 2, 'primary', true),
           (200, 2, 2, 'aa', false);

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email)
    VALUES(1000, 1, 'Inc', 'ray1@gmail.com'),
          (2000, 2, 'Inc', 'ray2@gmail.com');
