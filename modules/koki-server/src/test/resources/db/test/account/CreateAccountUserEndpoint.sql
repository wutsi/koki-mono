INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted)
    VALUES(100, 1, 'Inc', false),
          (110, 1, 'Inc', true),
          (111, 1, 'Inc', true);

INSERT INTO T_ACCOUNT_USER(id, tenant_fk, account_fk, username, password, salt)
    VALUES (111, 1, 111, 'roger.milla', '__secret__', '....1111....');
