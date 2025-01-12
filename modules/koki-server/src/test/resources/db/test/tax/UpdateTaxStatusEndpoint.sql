INSERT INTO T_TAX_TYPE(id, tenant_fk, name, title)
    VALUES (100, 1, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, status, fiscal_year, deleted)
    VALUES (100, 1, 100, 111, 2, 2014, false),
           (199, 1, 100, 111, 2, 2014, true),
           (200, 2, 100, 111, 2, 2014, false);
