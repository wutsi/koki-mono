INSERT INTO T_ACCOUNT(id, tenant_fk, name, user_fk)
    VALUES(100, 1, 'Sponsible', null),
          (110, 1, 'Inc', null),
          (111, 1, 'Yahoo', 111);

INSERT INTO T_USER(id, tenant_fk, type, username, email, password, salt, display_name)
    VALUES (111, 1, 2, 'yahoo', 'yahoo@gmail.com','__secret__', '....1111....', 'Yahoo');
