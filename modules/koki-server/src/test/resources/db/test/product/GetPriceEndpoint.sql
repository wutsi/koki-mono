INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'PRoduct !', null, true, false),
              (200, 1, 'XXX', 'PRoduct !', null, true, false);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, account_type_fk, name, amount, currency, active, deleted, start_at, end_at)
    VALUES (100, 1, 100, 111,  'P1',      1000, 'CAD', true, false, '2020-10-11', '2021-11-11'),
           (199, 1, 100, null, 'deleted', 1000, 'CAD', true, true,  null, null),
           (200, 2, 200, null, 'P2',      1000, 'CAD', true, false, null, null);
