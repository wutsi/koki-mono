INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted)
    VALUES(100, 1, 'Inc', false),
          (110, 1, 'Inc', true),
          (111, 1, 'Inc', true),
          (120, 1, 'Inc', true),
          (121, 1, 'Inc', true),
          (122, 1, 'Inc', true),
          (200, 2, 'Inc', false);

INSERT INTO T_ACCOUNT_USER(id, tenant_fk, account_fk, username, password, salt)
    VALUES (111, 1, 111, 'roger.milla', '__secret__', '....1111....'),
           (120, 1, 120, 'thomas.nkono', '__secret__', '....1111....'),
           (121, 1, 121, 'michel.platini', '__secret__', '....1111....'),
           (122, 1, 122, 'james.bond', '__secret__', '....1111....');
