INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 6, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, managed_by_fk)
    VALUES (111, 1, 'Account 1', 'ray1@gmail.com', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, status, fiscal_year, deleted)
    VALUES (100, 1, 100, 111, 2, 2014, false);
