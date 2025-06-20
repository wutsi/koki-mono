INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(33, 1, 'Inc', 'info@inc1.com', false);

