INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(33, 1, 'Inc', 'info@inc1.com', false);

INSERT INTO T_ROOM(id, tenant_fk, account_fk, status, title)
    VALUES (100, 1, 33, 0, 'Room 100'),
           (101, 1, 33, 1, 'Room 101'),
           (102, 1, 33, 2, 'Room 102'),
           (103, 1, 33, 3, 'Room 103');
