INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'PRoduct !', null, true, false),
              (110, 1, 'XXX', 'used in taxes', null, true, false),
              (199, 1, 'XXX', 'PRoduct !', null, true, true),
              (200, 2, 'XXX', 'PRoduct !', null, true, false);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, name, amount, currency, active, deleted)
    VALUES (110, 1, 100, 'P1', 1000, 'CAD', true, false);
