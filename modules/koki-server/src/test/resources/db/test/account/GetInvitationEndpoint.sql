INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted)
    VALUES(100, 1, 'Inc', false),
          (200, 2, 'Inc', false);

INSERT INTO T_INVITATION(id, tenant_fk, account_fk, created_by_fk)
    VALUES(101, 1, 100, 11),
          (201, 2, 200, null);
