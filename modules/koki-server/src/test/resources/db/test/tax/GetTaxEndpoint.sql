INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 6, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, technician_fk, assignee_fk, status, fiscal_year, deleted, start_at, due_at, description, total_revenue, currency, product_count)
    VALUES (100, 1, 100, 111, 110,  111,  112,  2, 2014, false, '2014-03-01 15:30:00', '2014-04-30 15:30:00', '2014 Tax Statements', 500, 'CAD', 2),
           (199, 1, 100, 111, null, null, null, 2, 2014, true, null, null, null, null, null, 0),
           (200, 2, 100, 111, null, null, null, 2, 2014, false, null, null, null, null, null, 0);
