INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted)
    VALUES(1000, 1, 'Inc',     false),
          (1100, 1, 'In Used', false),
          (1999, 1, 'Inc',     true),
          (2000, 2, 'Inc',     false);

INSERT INTO T_CONTACT(id, tenant_fk, account_fk, first_name, last_name)
    VALUES(110, 1, 1100, 'Ray', 'Sponsible');
