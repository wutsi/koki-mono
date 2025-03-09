INSERT INTO T_TRANSACTION(id, tenant_fk, type, payment_method_type, status, invoice_fk, amount, currency, gateway, error_code, supplier_error_code, description)
    VALUES (110, 1, 1, 1, 3, 1111, 500, 'CAD', 0, '1111', 'insufisant-funds', 'cash'),
           (119, 1, 1, 1, 3, 1111, 500, 'CAD', 0, '1111', 'insufisant-funds', 'no-payment-method-details'),

           (120, 1, 1, 2, 2, 1111, 500, 'CAD', 0, null, null, 'check'),
           (129, 1, 1, 2, 2, 1111, 500, 'CAD', 0, null, null, 'check'),

           (130, 1, 3, 3, 1, 1111, 500, 'CAD', 0, null, null, 'interact'),
           (139, 1, 1, 3, 1, 1111, 500, 'CAD', 0, null, null, null)
;

INSERT INTO T_PAYMENT_METHOD_CASH(id, tenant_fk, transaction_fk, collected_by_fk, collected_at) VALUES
    (110, 1, 110, 555, '2020-03-30');

INSERT INTO T_PAYMENT_METHOD_CHECK(id, tenant_fk, transaction_fk, check_number, bank_name, cleared_at) VALUES
    (120, 1, 120, '1234', 'TD Bank', '2020-03-30');

INSERT INTO T_PAYMENT_METHOD_INTERACT(id, tenant_fk, transaction_fk, reference_number, bank_name, sent_at, cleared_at) VALUES
    (130, 1, 130, '1234', 'TD Bank', '2020-01-03', '2020-01-30');
