INSERT INTO T_TAX_TYPE(id, tenant_fk, name, title)
    VALUES (100, 1, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, technician_fk, assignee_fk, status, fiscal_year, deleted, start_at, due_at, description)
    VALUES (100, 1, 100, 111, 110,  111,  112,  2, 2014, false, '2014-03-01 15:30:00', '2014-04-30 15:30:00', '2014 Tax Statements'),
           (199, 1, 100, 111, null, null, null, 2, 2014, true, null, null, null),
           (200, 2, 100, 111, null, null, null, 2, 2014, false, null, null, null);
