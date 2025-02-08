INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'P100', null, true, false),
              (110, 1, 'XXX', 'P110', null, true, false),
              (200, 1, 'XXX', 'P200', null, true, false);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, account_type_fk, amount, currency, active, deleted, start_at, end_at)
    VALUES (100, 1, 100, null, 1000, 'CAD', true,  false, '2020-10-11', '2021-11-11'),
           (101, 1, 100, null, 1000, 'USD', true,  false, '2020-01-01', null),
           (102, 1, 100, 111,  900,  'CAD', false, false, '2020-01-01', '2030-01-01'),

           (110, 1, 110, null, 1000, 'CAD', true,  false, null, null),

           (199, 1, 100, null, 1000, 'CAD', true,  true,  null, null),
           (200, 2, 200, null, 1000, 'CAD', true,  false, null, null);
