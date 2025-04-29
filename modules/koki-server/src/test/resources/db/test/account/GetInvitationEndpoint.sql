INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(100, 1, 'Inc', 'info@inc1.com',  false),
          (200, 2, 'Inc', 'info@inc2.com', false);

INSERT INTO T_INVITATION(id, tenant_fk, account_fk, created_by_fk)
    VALUES(101, 1, 100, 11),
          (201, 2, 200, null);
