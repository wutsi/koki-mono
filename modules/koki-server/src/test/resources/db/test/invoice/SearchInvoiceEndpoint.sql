INSERT INTO T_INVOICE(id, paynow_id, tenant_fk, order_fk, number, status, customer_account_fk, customer_name, customer_email, currency)
    VALUES
        (100, 'paynow100', 1, null, 10955, 2, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 'CAD'),
        (101, 'paynow101', 1, null, 10956, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 'CAD'),
        (102, 'paynow102', 1, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 'CAD'),
        (103, 'paynow103', 1, 8888, 10958, 4, null, 'Roger', 'roger.milla@gmail.com', 'CAD'),

        (200, 'paynow200', 2, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 'CAD')
;
