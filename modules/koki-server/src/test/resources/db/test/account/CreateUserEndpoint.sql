INSERT INTO T_ACCOUNT(id, tenant_fk, name, user_fk, language, email)
    VALUES(100, 1, 'Sponsible', null, 'fr', 'sponsible@gmail.com'),
          (110, 1, 'Inc', null, 'fr', 'inc@gmail.com'),
          (111, 1, 'Yahoo', 111, 'fr', 'yahoo@gmail.com');

INSERT INTO T_USER(id, tenant_fk, type, username, email, password, salt, display_name)
    VALUES (1110, 1, 2, 'yahoo', 'yahoo@gmail.com','__secret__', '....1111....', 'Yahoo');
