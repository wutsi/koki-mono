INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted)
    VALUES(100, 1, 'Inc', false),
          (200, 2, 'Inc', true);

INSERT INTO T_ACCOUNT_USER(id, tenant_fk, account_fk, username, password, salt, status)
    VALUES (101, 1, 100, 'roger.milla', '__secret__', '....1111....', 2),
           (201, 2, 100, 'roger.milla', '__secret__', '....1111....', 3);
