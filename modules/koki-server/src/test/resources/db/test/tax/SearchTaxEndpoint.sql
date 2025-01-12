INSERT INTO T_TAX_TYPE(id, tenant_fk, name, title)
    VALUES (100, 1, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (100, 1, 'Account 1', 11),
           (110, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, created_by_fk, status, fiscal_year, deleted)
    VALUES (100, 1, 100, 111, 11,  55, 4, 2014, false),
           (101, 1, 100, 111, 11,  44, 3, 2015, false),
           (102, 1, 100, 112, 12,  55, 3, 2014, false),
           (110, 1, 110, 110, 12,  55, 2, 2014, false),
           (111, 1, 110, 110, 11,  55, 2, 2015, false),
           (199, 1, 100, 111, null, 55, 2, 2014, true),
           (200, 2, 100, 111, null, 22, 2, 2014, false);
