INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(33, 1, 'Inc', 'info@inc1.com', false);

INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, city_fk, state_fk, country, title, deleted, deleted_at, deleted_by_fk)
    VALUES (111, 1, 33, 1, 1001, 100, 'CA', 'Room A', false, null, null),
           (112, 1, 33, 1, 1001, 100, 'CA', 'Room A', true, '2020-01-10 15:30', 3333);
