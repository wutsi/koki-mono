INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'PRoduct !', null, true, false);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, name, amount, currency, active, deleted)
    VALUES (100, 1, 100, 'P1', 1000, 'CAD', true, false),
           (101, 1, 100, 'P2', 1010, 'CAD', true, false),
           (102, 1, 100, 'P2', 1020, 'CAD', true, true);
