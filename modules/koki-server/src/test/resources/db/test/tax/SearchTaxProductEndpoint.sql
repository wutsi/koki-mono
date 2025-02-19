INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes'),
           (110, 1, 6, 'CORPORATE', 'Corporate Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, managed_by_fk)
    VALUES (111, 1, 'Account 1', 11),
           (222, 2, 'Account 1', 11);

INSERT INTO T_TAX(id, tenant_fk, tax_type_fk, account_fk, accountant_fk, technician_fk, assignee_fk, status, fiscal_year, deleted, start_at, due_at, description)
    VALUES (100, 1, 100,  111, 110,   111,   112,   2, 2014, false, '2014-03-01 15:30:00', '2014-04-30 15:30:00', '2014 Tax Statements'),
           (200, 2, null, 222, null,  null,  null,  2, 2014, false, '2014-03-01 15:30:00', '2014-04-30 15:30:00', null);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, amount, currency)
    VALUE (11100, 1, 111, 150, 'CAD'),
          (11200, 1, 111, 150, 'CAD'),
          (11300, 1, 111, 150, 'CAD'),
          (20000, 2, 200, 150, 'CAD');

INSERT INTO T_TAX_PRODUCT(id, tenant_fk, tax_fk, product_fk, unit_price_fk, quantity, unit_price, sub_total, currency, description)
    VALUES (100, 1, 100, 111, 11100, 3, 150, 450, 'CAD', 'Yo man...'),
           (110, 1, 100, 112, 11200, 1, 100, 100, 'CAD', 'Yo man...'),
           (120, 1, 100, 113, 11300, 3, 50,  150, 'CAD', 'Yo man...'),
           (200, 2, 200, 200, 20001, 1, 100, 100, 'CAD', null);
