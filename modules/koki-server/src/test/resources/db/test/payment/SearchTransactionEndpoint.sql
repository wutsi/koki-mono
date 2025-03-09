INSERT INTO T_TRANSACTION(id, tenant_fk, type, payment_method_type, status, invoice_fk, amount, currency, created_at)
    VALUES (100, 1, 1, 1, 1, 555, 500, 'CAD', '2020-01-02'),
           (110, 1, 1, 2, 2, 555, 100, 'CAD', '2020-01-20'),
           (120, 1, 2, 3, 1, 111, 500, 'CAD', '2021-01-01'),
           (121, 1, 3, 3, 2, 222, 500, 'CAD', '2022-01-01'),
           (130, 1, 2, 1, 1, 333, 500, 'CAD', '2023-01-01'),
           (200, 2, 2, 3, 2, 444, 500, 'CAD', '2020-01-01');
