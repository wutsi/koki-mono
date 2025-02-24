INSERT INTO T_INVOICE(id, tenant_fk, tax_fk, order_fk, number, status, customer_account_fk, customer_name, customer_email, currency)
    VALUES
        (100, 1, 7777, null, 10955, 2, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 'CAD'),
        (101, 1, 7778, null, 10956, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 'CAD'),
        (102, 1, 7779, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 'CAD'),
        (103, 1, null, 8888, 10958, 4, null, 'Roger', 'roger.milla@gmail.com', 'CAD'),

        (200, 2, 7779, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 'CAD')
;
