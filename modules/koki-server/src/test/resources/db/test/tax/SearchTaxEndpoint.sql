INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 6, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (100, 1, 'Account 1', 11),
           (110, 1, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, technician_fk, assignee_fk, created_by_fk, status, fiscal_year, deleted, start_at, due_at)
    VALUES (100, 1, 100, 111, 11,   null, 110,  55, 4, 2014, false, '2020-10-01', '2020-12-31'),
           (101, 1, 100, 111, null, 11,   null, 44, 3, 2015, false, '2020-10-02', '2020-12-10'),
           (102, 1, 100, 112, 12,   null, 110,  55, 3, 2014, false, '2020-11-03', '2021-04-30'),
           (110, 1, 110, 110, 12,   null, null, 55, 2, 2014, false, '2020-11-04', '2021-04-30'),
           (111, 1, 110, 110, 11,   null, null, 55, 2, 2015, false, '2020-12-05', '2020-12-01'),
           (199, 1, 100, 111, null, null, null, 55, 2, 2014, true,  '2020-12-06', '2021-12-31'),
           (200, 2, 100, 111, null, null, null, 22, 2, 2014, false, '2020-10-07', '2021-12-31');
