INSERT INTO T_TAX_TYPE(id, tenant_fk, name, title)
    VALUES (100, 1, 'PERSONAL', 'Personal Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);
