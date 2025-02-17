INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 6, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, technician_fk, assignee_fk, status, fiscal_year, deleted, start_at, due_at, description)
    VALUES (100, 1, 100, 111, 110,  111,  112,  2, 2014, false, '2014-03-01 15:30:00', '2014-04-30 15:30:00', '2014 Tax Statements');

INSERT INTO T_TAX_PRODUCT(tenant_fk, tax_fk, product_fk, quantity, unit_price, sub_total)
    VALUES (1, 100, 111, 3, 150, 300);
