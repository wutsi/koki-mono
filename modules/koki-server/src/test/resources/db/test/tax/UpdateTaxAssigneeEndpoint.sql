INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, assignee_fk, fiscal_year, deleted)
    VALUES (100, 1, 100, 111, null, 2014, false),
           (110, 1, 100, 111, 111, 2014, false);
