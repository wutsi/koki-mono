INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'PRoduct !', null, true, false);

INSERT INTO T_PRICE(id, tenant_fk, product_fk, name, amount, currency, active, deleted)
    VALUES (100, 1, 100, 'P1', 1000, 'CAD', true, false),
           (110, 1, 100, 'P1', 1000, 'CAD', true, false);


INSERT INTO T_TAX(id, tenant_fk, account_fk)
    VALUES (110, 1, 111);

INSERT INTO T_TAX_PRODUCT(tenant_fk, tax_fk, product_fk, unit_price_fk, quantity, unit_price, sub_total, currency)
    VALUES (1, 110, 100, 110, 3, 150, 450, 'CAD');
